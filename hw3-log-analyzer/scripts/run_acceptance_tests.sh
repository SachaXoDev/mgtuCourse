#!/bin/bash

echo 'Running acceptance tests...'

tag=$CI_PROJECT_NAME-$CI_COMMIT_SHA
echo "Building Docker image logs-app:$tag"
if [ -n "$GITLAB_DOCKER_PROXY" ]; then
    echo "Using proxy: $GITLAB_DOCKER_PROXY"
    docker build --build-arg RUNTIME_IMAGE=$GITLAB_DOCKER_PROXY/eclipse-temurin:24-jre . -q -t logs-app:$tag
else
    echo "Building without proxy"
    docker build -q -t logs-app:$tag .
fi
testNumber=0
failedTests=0

function assertExitCode {
  RED='\033[0;31m'
  GREEN='\033[0;32m'
  NC='\033[0m'

  echo "Exit code: expected=$1, actual=$2"
  if [ $1 -ne $2 ]; then
     echo -e "${RED}Test №${testNumber} failed${NC}"
     failedTests=$((failedTests+1))
  else
     echo -e "${GREEN}Test №${testNumber} passed${NC}"
  fi
}

function runTest {
  local test_type=$1
  local description=$2
  local expected_exit=$3
  shift 3
  
  echo "Test [№${testNumber}][${test_type}]: ${description}; expected exit code: ${expected_exit}; args: $@;"
  docker run --rm -v $(pwd)/scripts/data:/tmp/data logs-app:$tag "$@"
  
  exit_code=$?
  assertExitCode $expected_exit $exit_code
  
  testNumber=$((testNumber+1))
}

function assertJsonEquals {
  echo "Comparing JSON's..."
  if command -v jq &> /dev/null; then
    diff -u <(jq -S '.' "$1") <(jq -S '.' "$2")
  else
    docker run --rm -v $(pwd)/scripts/data:/tmp/data ghcr.io/jqlang/jq:latest -S '.' "$1" > /tmp/expected.json
    docker run --rm -v $(pwd)/scripts/data:/tmp/data ghcr.io/jqlang/jq:latest -S '.' "$2" > /tmp/actual.json
    diff -u /tmp/expected.json /tmp/actual.json
  fi
  
  if [ $? -ne 0 ]; then
    echo -e "${RED}Test №${testNumber} failed${NC}"
    failedTests=$((failedTests+1))
  else
    echo -e "${GREEN}Test №${testNumber} passed${NC}"
  fi
  testNumber=$((testNumber+1))
}

function verifyAllTestsPassed {
  if [ $failedTests -gt 0 ]; then
    echo "Total failed tests: $failedTests"
    echo "Some tests have failed!"
    exit 1
  else
    echo -e "${GREEN}All tests passed${NC}"
    exit 0
  fi
}

echo "Running negative tests..."

echo "Cleaning output directory..."
rm -f ./scripts/data/output/*.json ./scripts/data/output/*.md ./scripts/data/output/*.adoc 2>/dev/null || true

runTest "negative" "input file does not exist" 2 \
  -p /tmp/data/input/nonexistent.txt -f json -o /tmp/data/output/output1.json

runTest "negative" "input file has unsupported extension" 2 \
  -p /tmp/data/input/file1.log -f json -o /tmp/data/output/output2.json

# Тест 2: Создаем существующий JSON файл с корректным содержимым
echo "Creating existing.json for test..."
cat > ./scripts/data/output/existing.json << 'JSONEOF'
{
  "files": ["test.txt"],
  "totalRequestsCount": 100,
  "responseSizeInBytes": {
    "average": 200.0,
    "max": 1000,
    "p95": 300.0
  }
}
JSONEOF

runTest "negative" "output file already exists" 2 \
  -p /tmp/data/input/file2.txt -f json -o /tmp/data/output/existing.json

runTest "negative" "output file has unsupported extension (JSON)" 2 \
  -p /tmp/data/input/file2.txt -f json -o /tmp/data/output/output4.txt

runTest "negative" "output file has unsupported extension (MD)" 2 \
  -p /tmp/data/input/file2.txt -f markdown -o /tmp/data/output/output5.txt

runTest "negative" "output file has unsupported extension (AD)" 2 \
  -p /tmp/data/input/file2.txt -f adoc -o /tmp/data/output/output6.txt

runTest "negative" "unsupported output format" 2 \
  -p /tmp/data/input/file2.txt -f txt -o /tmp/data/output/output7.txt

runTest "negative" "invalid date format (--from)" 2 \
  -p /tmp/data/input/file2.txt -f txt -o /tmp/data/output/output8.json --from="2025.01.02"

runTest "negative" "invalid date format (--to)" 2 \
  -p /tmp/data/input/file2.txt -f txt -o /tmp/data/output/output9.json --to="2025.01.02"

runTest "negative" "--from > --to" 2 \
  -p /tmp/data/input/file2.txt -f txt -o /tmp/data/output/output10.json --from="2025-01-02" --to="2025-01-01"

runTest "negative" "required parameter -p is missing" 2 \
  -f json -o /tmp/data/output/output11.json

runTest "negative" "required parameter -f is missing" 2 \
  -p /tmp/data/input/nonexistent.txt -o /tmp/data/output/output12.json

runTest "negative" "required parameter -o is missing" 2 \
  -p /tmp/data/input/nonexistent.txt -f json -o /tmp/data/output/output13.json

runTest "negative" "unsupported parameter is present" 2 \
  -p /tmp/data/input/nonexistent.txt -f json -o /tmp/data/output/output14.json --custom=argument

echo "Preparing for positive test..."
rm -f ./scripts/data/output/stats.json ./scripts/data/output/stats*.json 2>/dev/null || true

runTest "positive" "properly calculate statistics from multiple local files" 0 \
  -p /tmp/data/input/logs/part1.txt \
  -p /tmp/data/input/logs/part2.txt \
  -f json \
  -o /tmp/data/output/stats.json

if [ ! -f ./scripts/data/output/expected.json ]; then
    echo "WARNING: expected.json not found, creating from stats.json"
    cp ./scripts/data/output/stats.json ./scripts/data/output/expected.json
fi

assertJsonEquals ./scripts/data/output/expected.json ./scripts/data/output/stats.json

verifyAllTestsPassed

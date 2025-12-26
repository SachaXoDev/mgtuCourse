package academy;

import java.time.LocalDateTime;
import java.util.Objects;

public class NginxLogRecord {
    private String remoteAddr;
    private String remoteUser;
    private LocalDateTime timeLocal;
    private String requestMethod;
    private String resource;
    private String protocol;
    private int status;
    private long bodyBytesSent;
    private String httpReferer;
    private String httpUserAgent;

    public NginxLogRecord() {}

    public String getRemoteAddr() {
        return remoteAddr;
    }

    public void setRemoteAddr(String remoteAddr) {
        this.remoteAddr = remoteAddr;
    }

    public String getRemoteUser() {
        return remoteUser;
    }

    public void setRemoteUser(String remoteUser) {
        this.remoteUser = remoteUser;
    }

    public LocalDateTime getTimeLocal() {
        return timeLocal;
    }

    public void setTimeLocal(LocalDateTime timeLocal) {
        this.timeLocal = timeLocal;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getBodyBytesSent() {
        return bodyBytesSent;
    }

    public void setBodyBytesSent(long bodyBytesSent) {
        this.bodyBytesSent = bodyBytesSent;
    }

    public String getHttpReferer() {
        return httpReferer;
    }

    public void setHttpReferer(String httpReferer) {
        this.httpReferer = httpReferer;
    }

    public String getHttpUserAgent() {
        return httpUserAgent;
    }

    public void setHttpUserAgent(String httpUserAgent) {
        this.httpUserAgent = httpUserAgent;
    }

    @Override
    public String toString() {
        return String.format("NginxLogRecord{resource='%s', status=%d, bytes=%d}", resource, status, bodyBytesSent);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NginxLogRecord that = (NginxLogRecord) o;
        return status == that.status
                && bodyBytesSent == that.bodyBytesSent
                && Objects.equals(remoteAddr, that.remoteAddr)
                && Objects.equals(timeLocal, that.timeLocal)
                && Objects.equals(resource, that.resource);
    }

    @Override
    public int hashCode() {
        return Objects.hash(remoteAddr, timeLocal, resource, status, bodyBytesSent);
    }
}

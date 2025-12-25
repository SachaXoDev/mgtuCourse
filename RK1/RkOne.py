class Orchestra:
    def __init__(self, orchestra_id, name):
        self.orchestra_id = orchestra_id
        self.name = name

    def __repr__(self):
        return f"Orchestra({self.orchestra_id}, '{self.name}')"

class MusicPiece:
    def __init__(self, piece_id, title, composer, duration, orchestra_id):
        self.piece_id = piece_id
        self.title = title
        self.composer = composer
        self.duration = duration
        self.orchestra_id = orchestra_id

    def __repr__(self):
        return f"MusicPiece({self.piece_id}, '{self.title}', '{self.composer}', {duration}min, orch_id: {self.orchestra_id})"

class OrchestraPieces:
    def __init__(self, orchestra_id, piece_id):
        self.orchestra_id = orchestra_id
        self.piece_id = piece_id

    def __repr__(self):
        return f"OrchestraPieces(orch_id: {self.orchestra_id}, piece_id: {self.piece_id})"

orchestras = [
    Orchestra(1, "Большой симфонический оркестр"),
    Orchestra(2, "Камерный оркестр Филармонии"),
    Orchestra(3, "Струнный отдел оркестра"),
    Orchestra(4, "Духовой оркестр")
]

music_pieces = [
    MusicPiece(1, "Симфония №5", "Бетховен", 32, 1),
    MusicPiece(2, "Времена года", "Вивальди", 45, 2),
    MusicPiece(3, "Лебединое озеро", "Чайковский", 28, 1),
    MusicPiece(4, "Кармен", "Бизе", 25, 3),
    MusicPiece(5, "Токката и фуга", "Бах", 9, 2),
    MusicPiece(6, "Ноктюрн", "Шопен", 6, 3),
    MusicPiece(7, "Марш", "Суза", 4, 4),
    MusicPiece(8, "Увертюра 1812", "Чайковский", 15, 4)
]

orchestra_pieces = [
    OrchestraPieces(1, 1),
    OrchestraPieces(1, 3),
    OrchestraPieces(2, 2),
    OrchestraPieces(2, 5),
    OrchestraPieces(3, 4),
    OrchestraPieces(3, 6),
    OrchestraPieces(4, 7),
    OrchestraPieces(4, 8),
    OrchestraPieces(1, 8),
    OrchestraPieces(2, 6)
]

def task1_one_to_many():
    print(" ЗАПРОС 1 ")
    print("Список всех произведений и их оркестров (сортировка по оркестрам):")

    result = []
    for piece in music_pieces:
        orchestra = next((o for o in orchestras if o.orchestra_id == piece.orchestra_id), None)
        if orchestra:
            result.append((piece, orchestra))

    result.sort(key=lambda x: x[1].name)

    for piece, orchestra in result:
        print(f"Оркестр: {orchestra.name:<35} Произведение: {piece.title:<20} Композитор: {piece.composer:<15} Длительность: {piece.duration}min")

def task2_one_to_many():
    print("\nЗАПРОС 2 ")
    print("Список оркестров с суммарной длительностью произведений:")

    orchestra_durations = {}
    for piece in music_pieces:
        if piece.orchestra_id not in orchestra_durations:
            orchestra_durations[piece.orchestra_id] = 0
        orchestra_durations[piece.orchestra_id] += piece.duration

    result = []
    for orchestra in orchestras:
        total_duration = orchestra_durations.get(orchestra.orchestra_id, 0)
        result.append((orchestra, total_duration))

    result.sort(key=lambda x: x[1])

    for orchestra, total_duration in result:
        print(f"Оркестр: {orchestra.name:<35} Суммарная длительность: {total_duration}min")

def task3_many_to_many():
    print("\n ЗАПРОС 3 ")
    print("Список оркестров, содержащих 'оркестр' в названии, и их произведения:")

    filtered_orchestras = [o for o in orchestras if 'оркестр' in o.name.lower()]

    result = []
    for orchestra in filtered_orchestras:
        orchestra_pieces_list = []
        for op in orchestra_pieces:
            if op.orchestra_id == orchestra.orchestra_id:
                piece = next((p for p in music_pieces if p.piece_id == op.piece_id), None)
                if piece:
                    orchestra_pieces_list.append(piece)

        if orchestra_pieces_list:
            result.append((orchestra, orchestra_pieces_list))

    result.sort(key=lambda x: x[0].name)

    for orchestra, pieces_list in result:
        print(f"\nОркестр: {orchestra.name}")
        for piece in pieces_list:
            print(f"  → Произведение: {piece.title:<25} Композитор: {piece.composer:<15} Длительность: {piece.duration}min")

def print_all_data():
    print("ИСХОДНЫЕ ДАННЫЕ:")
    print("\nОркестры:")
    for o in orchestras:
        print(f"  {o}")

    print("\nМузыкальные произведения:")
    for p in music_pieces:
        print(f"  ID: {p.piece_id}, '{p.title}' - {p.composer}, {p.duration}min, orch_id: {p.orchestra_id}")

    print("\nСвязи многие-ко-многим:")
    for op in orchestra_pieces:
        print(f"  {op}")

if __name__ == "__main__":
    print("РУБЕЖНЫЙ КОНТРОЛЬ №1")
    print("Предметная область: Музыкальное произведение - Оркестр")
    print("Вариант: 18 (А)\n")

    print_all_data()
    print("\n" + "="*60)

    task1_one_to_many()
    task2_one_to_many()
    task3_many_to_many()

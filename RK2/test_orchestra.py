import unittest

from RKTwo import Orchestra, MusicPiece, OrchestraPieces, get_orchestra_pieces, get_orchestra_total_durations, get_orchestras_with_keyword


class TestOrchestraQueries(unittest.TestCase):

    def setUp(self):
        """Подготовка данных для тестов."""
        self.orchestras = [
            Orchestra(1, "Большой симфонический оркестр"),
            Orchestra(2, "Камерный оркестр Филармонии"),
            Orchestra(3, "Струнный отдел оркестра"),
            Orchestra(4, "Духовой оркестр")
        ]

        self.music_pieces = [
            MusicPiece(1, "Симфония №5", "Бетховен", 32, 1),
            MusicPiece(2, "Времена года", "Вивальди", 45, 2),
            MusicPiece(3, "Лебединое озеро", "Чайковский", 28, 1),
            MusicPiece(4, "Кармен", "Бизе", 25, 3),
            MusicPiece(5, "Токката и фуга", "Бах", 9, 2),
            MusicPiece(6, "Ноктюрн", "Шопен", 6, 3),
            MusicPiece(7, "Марш", "Суза", 4, 4),
            MusicPiece(8, "Увертюра 1812", "Чайковский", 15, 4)
        ]

        self.orchestra_pieces = [
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

    def test_get_orchestra_pieces(self):
        """Тест запроса 1: соответствие оркестров и произведений."""
        result = get_orchestra_pieces(self.orchestras, self.music_pieces)

        self.assertEqual(len(result), len(self.music_pieces))

        orchestra_names = [orchestra.name for _, orchestra in result]
        self.assertEqual(orchestra_names, sorted(orchestra_names))

        for piece, orchestra in result:
            self.assertEqual(piece.orchestra_id, orchestra.orchestra_id)

    def test_get_orchestra_total_durations(self):
        """Тест запроса 2: суммарная длительность произведений по оркестрам."""
        result = get_orchestra_total_durations(self.orchestras, self.music_pieces)

        durations = [total for _, total in result]
        self.assertEqual(durations, sorted(durations))

        total_duration_dict = {orchestra.orchestra_id: total for orchestra, total in result}
        self.assertEqual(total_duration_dict[1], 32 + 28)
        self.assertEqual(total_duration_dict[2], 45 + 9)
        self.assertEqual(total_duration_dict[3], 25 + 6)
        self.assertEqual(total_duration_dict[4], 4 + 15)

    def test_get_orchestras_with_keyword(self):
        """Тест запроса 3: фильтрация оркестров по ключевому слову."""
        result = get_orchestras_with_keyword(self.orchestras, self.music_pieces, self.orchestra_pieces, 'оркестр')

        for orchestra, _ in result:
            self.assertIn('оркестр', orchestra.name.lower())

        for orchestra, pieces in result:
            self.assertGreater(len(pieces), 0)

        orchestra_names = [orchestra.name for orchestra, _ in result]
        self.assertEqual(orchestra_names, sorted(orchestra_names))

        self.assertEqual(len(result), 4)


if __name__ == '__main__':
    unittest.main()

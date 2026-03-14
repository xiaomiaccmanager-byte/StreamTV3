package com.streamtv
object DemoData {
    val channels = listOf(
        Channel(0, "БОЕВИК", "&#x1F4A5;", listOf(
            Show("Ходячие мертвецы", listOf(Episode("S11E01",durationMin=45),Episode("S11E02",durationMin=45),Episode("S11E03",durationMin=45))),
            Show("Игра Престолов", listOf(Episode("S08E01",durationMin=60),Episode("S08E02",durationMin=60),Episode("S08E03",durationMin=82))),
            Show("24", listOf(Episode("S08E01 — 1:00-2:00",durationMin=44),Episode("S08E02 — 2:00-3:00",durationMin=44)))
        )),
        Channel(1, "ФАНТАСТИКА", "&#x1F680;", listOf(
            Show("Мандалорец", listOf(Episode("S03E01",durationMin=40),Episode("S03E02",durationMin=42),Episode("S03E03",durationMin=38))),
            Show("Очень странные дела", listOf(Episode("S04E01",durationMin=75),Episode("S04E02",durationMin=64))),
            Show("Westworld", listOf(Episode("S04E01",durationMin=60),Episode("S04E02",durationMin=55)))
        )),
        Channel(2, "ДРАМА", "&#x1F3AD;", listOf(
            Show("Во все тяжкие", listOf(Episode("S05E01",durationMin=47),Episode("S05E02",durationMin=47),Episode("S05E03",durationMin=47))),
            Show("Чернобыль", listOf(Episode("S01E01 — 1:23:45",durationMin=60),Episode("S01E02",durationMin=65)))
        )),
        Channel(3, "КОМЕДИЯ", "&#x1F604;", listOf(
            Show("Теория большого взрыва", listOf(Episode("S12E01",durationMin=22),Episode("S12E02",durationMin=22),Episode("S12E03",durationMin=22))),
            Show("Офис", listOf(Episode("S09E01",durationMin=22),Episode("S09E02",durationMin=22)))
        )),
        Channel(4, "КРИМИНАЛ", "&#x1F575;&#xFE0F;", listOf(
            Show("Острые козырьки", listOf(Episode("S06E01",durationMin=60),Episode("S06E02",durationMin=55))),
            Show("Нарко", listOf(Episode("S03E01",durationMin=55),Episode("S03E02",durationMin=52)))
        )),
        Channel(5, "АНИМЕ", "&#x26E9;", listOf(
            Show("Атака Титанов", listOf(Episode("S04E25",durationMin=25),Episode("S04E26",durationMin=25))),
            Show("Клинок демонов", listOf(Episode("S03E01",durationMin=24),Episode("S03E02",durationMin=24)))
        ))
    )
}
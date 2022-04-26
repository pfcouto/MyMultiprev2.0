package pt.ipleiria.estg.dei.pi.mymultiprev.repositories

import pt.ipleiria.estg.dei.pi.mymultiprev.data.model.TesteLazyColumn

class TesteLazyColumRepository {
    fun getAllData(): List<TesteLazyColumn> {
        return listOf(
            TesteLazyColumn(
                firstString = "John",
                secondString = "Doe"
            ),
            TesteLazyColumn(
                firstString = "Maria",
                secondString = "Garcia"
            ),
            TesteLazyColumn(
                firstString = "James",
                secondString = "Johnson"
            ),
            TesteLazyColumn(
                firstString = "Michael",
                secondString = "Brown"
            ),
            TesteLazyColumn(
                firstString = "Robert",
                secondString = "Davis"
            ),
            TesteLazyColumn(
                firstString = "Jenifer",
                secondString = "Miller"
            ),
            TesteLazyColumn(
                firstString = "Sarah",
                secondString = "Lopez"
            ),
            TesteLazyColumn(
                firstString = "Charles",
                secondString = "Wilson"
            ),
            TesteLazyColumn(
                firstString = "Daniel",
                secondString = "Taylor"
            ),
            TesteLazyColumn(
                firstString = "Mark",
                secondString = "Lee"
            ),
        )
    }
}
package io.github.ejiang.visittracker.networking

// merely a DAO w/ Room for now

interface Repo {
    fun saveVisit()
    fun deleteVisit()
    fun readVisit()
    fun readVisits()
}

class RestRepo : Repo {
    override fun saveVisit() {
    }

    override fun readVisit() {
    }

    override fun deleteVisit() {
    }

    override fun readVisits() {
    }
}

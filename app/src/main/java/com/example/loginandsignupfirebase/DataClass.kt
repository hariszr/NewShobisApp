package com.example.loginandsignupfirebase

class DataClass {
    var dataFarmer: String? = null
    var dataVariety: String? = null
    var dataWeight: String? = null
    var dataDate: String? = null

    constructor(dataFarmer: String?, dataVariety: String?, dataWeight: String?, dataDate: String?){
        this.dataFarmer = dataFarmer
        this.dataVariety = dataVariety
        this.dataWeight = dataWeight
        this.dataDate = dataDate
    }
}
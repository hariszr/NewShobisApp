package com.example.loginandsignupfirebase

class DataClass {
    var pid: String? = null
    var dataFarmer: String? = null
    var dataVariety: String? = null
    var dataWeight: String? = null
    var dataDate: String? = null

    constructor(pid: String?, dataFarmer: String?, dataVariety: String?, dataWeight: String?, dataDate: String?){
        this.pid = pid
        this.dataFarmer = dataFarmer
        this.dataVariety = dataVariety
        this.dataWeight = dataWeight
        this.dataDate = dataDate
    }

    constructor(){

    }
}
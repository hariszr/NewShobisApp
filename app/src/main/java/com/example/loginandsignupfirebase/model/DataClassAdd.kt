package com.example.loginandsignupfirebase.model

class DataClassAdd {
    var pid: String? = null
    var dataQrCode: String? = null
    var dataArriveDate: String? = null
    var dataIncomingWeight: String? = null
    var dataGrade: String? = null
    var dataPrice: String? = null

    var dataOutgoingWeight: String? = null
    var dataWeightLoss: String? = null
    var dataOutgoingDate: String? = null

    var dataDateCreate: String? = null

    constructor(pid: String?, dataQrCode: String?, dataArriveDate: String?, dataIncomingWeight: String?, dataGrade: String?, dataPrice: String?,
                dataOutgoingWeight: String?, dataWeightLoss: String?, dataOutgoingDate: String?, dataDateCreate: String?){

        this.pid = pid
        this.dataQrCode = dataQrCode
        this.dataArriveDate = dataArriveDate
        this.dataIncomingWeight = dataIncomingWeight
        this.dataGrade = dataGrade
        this.dataPrice = dataPrice

        this.dataOutgoingWeight = dataOutgoingWeight
        this.dataWeightLoss = dataWeightLoss
        this.dataOutgoingDate = dataOutgoingDate

        this.dataDateCreate = dataDateCreate

    }
    constructor(){

    }
}
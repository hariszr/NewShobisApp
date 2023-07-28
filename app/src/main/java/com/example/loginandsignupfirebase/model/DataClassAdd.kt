package com.example.loginandsignupfirebase.model

class DataClassAdd {
    var pid: String? = null
    var dataQrCode: String? = null
    var dataQrCodeUpdate: String? = null
    var dataArriveDate: String? = null
    var dataIncomingWeight: String? = null
    var dataGrade: String? = null
    var dataPrice: String? = null

    var dataOutgoingWeight: String? = null
    var dataWeightLoss: String? = null
    var dataOutgoingDate: String? = null

    var dataDateCreate: String? = null

    var dataNotes: String? = null

    var dataNameCreator: String? = null
    var dataActor: String? = null
    var dataEmail: String? = null
    var dataGender: String? = null
    var dataCompany: String? = null
    var dataAddress: String? = null

    constructor(pid: String?, dataQrCode: String?, dataArriveDate: String?, dataIncomingWeight: String?, dataGrade: String?, dataPrice: String?,
                dataOutgoingWeight: String?, dataWeightLoss: String?, dataOutgoingDate: String?, dataDateCreate: String?,
                dataNotes: String?,
                dataNameCreator: String?, dataActor: String?, dataEmail: String?, dataGender: String?, dataCompany: String?, dataAddress: String?
    ){

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

        this.dataNotes = dataNotes

        this.dataNameCreator = dataNameCreator
        this.dataActor = dataActor
        this.dataEmail = dataEmail
        this.dataGender = dataGender
        this.dataCompany = dataCompany
        this.dataAddress = dataAddress

    }
    constructor(dataQrCodeUpdate: String?) {
        this.dataQrCodeUpdate = dataQrCodeUpdate
    }
    constructor()
}
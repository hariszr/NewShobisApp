package com.example.loginandsignupfirebase.model

class DataClassAdd {
    var pid: String? = null
    var dataQrCode: String? = null
    var dataQrCodeUpdate: String? = null
    var dataArriveDate: String? = null
    var dataIncomingWeight: String? = null
    var dataPurchasePrice: String? = null

    var dataOutgoingDate: String? = null
    var dataOutgoingWeight: String? = null
    var dataGrade: String? = null
    var dataHandling: String? = null
    var dataHandlingFee: String? = null
    var dataWeightLoss: String? = null
    var dataSellingPrice: String? = null
    var dataDistribution: String? = null

    var dataDateCreate: String? = null

    var dataNotes: String? = null

    var dataNameCreator: String? = null
    var dataActor: String? = null
    var dataEmail: String? = null
    var dataGender: String? = null
    var dataCompany: String? = null
    var dataAddress: String? = null

    constructor(pid: String?, dataQrCode: String?, dataArriveDate: String?, dataIncomingWeight: String?, dataPurchasePrice: String?,
                dataOutgoingDate: String?, dataOutgoingWeight: String?, dataGrade: String?, dataHandling: String?, dataHandlingFee: String?, dataWeightLoss: String?, dataSellingPrice: String?, dataDistribution: String?,
                dataDateCreate: String?,
                dataNotes: String?,
                dataNameCreator: String?, dataActor: String?, dataEmail: String?, dataGender: String?, dataCompany: String?, dataAddress: String?
    ){

        this.pid = pid
        this.dataQrCode = dataQrCode
        this.dataArriveDate = dataArriveDate
        this.dataIncomingWeight = dataIncomingWeight
        this.dataPurchasePrice = dataPurchasePrice

        this.dataOutgoingDate = dataOutgoingDate
        this.dataOutgoingWeight = dataOutgoingWeight
        this.dataGrade = dataGrade
        this.dataHandling = dataHandling
        this.dataHandlingFee = dataHandlingFee
        this.dataWeightLoss = dataWeightLoss
        this.dataSellingPrice = dataSellingPrice
        this.dataDistribution = dataDistribution

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
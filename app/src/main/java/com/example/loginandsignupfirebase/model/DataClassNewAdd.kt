package com.example.loginandsignupfirebase.model

class DataClassNewAdd {
    var key: String? = null
    var pid: String? = null
    var dataQrCode: String? = null
    var dataQrCodeUpdate: String? = null
    var dataPicProduct: String? = null
    var dataVariety: String? = null
    var dataWeight: String? = null
    var dataGrade: String? = null
    var dataSellingPrice: String? = null
    var dataHandling: String? = null
    var dataHandlingFee: String? = null

    var dataFarmer: String? = null
    var dataDay: String? = null
    var dataPlantingArea: String? = null
    var dataFertilizer: String? = null
    var dataPesticides: String? = null
    var dataWeightFarmers: String? = null
    var dataPriceFarmers: String? = null

    var dataDateCreate: String? = null

    var dataNotes: String? = null

    var dataNameCreator: String? = null
    var dataActor: String? = null
    var dataEmail: String? = null
    var dataGender: String? = null
    var dataCompany: String? = null
    var dataAddress: String? = null

    constructor(pid: String?, dataQrCode: String?, dataQrCodeUpdate: String?, dataPicProduct: String?, dataVariety: String?, dataWeight: String?, dataGrade: String?, dataSellingPrice: String?, dataHandling: String?, dataHandlingFee: String?,
                dataFarmer: String?, dataDay: String?, dataPlantingArea: String?, dataFertilizer: String?, dataPesticides: String?, dataWeightFarmers: String?, dataPriceFarmers: String?,
                dataDateCreate: String?,
                dataNotes: String?,
                dataNameCreator: String?, dataActor: String?, dataEmail: String?, dataGender: String?, dataCompany: String?, dataAddress: String?){

        this.pid = pid
        this.dataQrCode = dataQrCode
        this.dataQrCodeUpdate = dataQrCode //dah bener ini gini
        this.dataPicProduct = dataPicProduct
        this.dataVariety = dataVariety
        this.dataWeight = dataWeight
        this.dataGrade = dataGrade
        this.dataSellingPrice = dataSellingPrice
        this.dataHandling = dataHandling
        this.dataHandlingFee = dataHandlingFee

        this.dataFarmer = dataFarmer
        this.dataDay = dataDay
        this.dataPlantingArea = dataPlantingArea
        this.dataFertilizer = dataFertilizer
        this.dataPesticides = dataPesticides
        this.dataWeightFarmers = dataWeightFarmers //dah bener ini gini
        this.dataPriceFarmers = dataPriceFarmers

        this.dataDateCreate = dataDateCreate

        this.dataNotes = dataNotes

        this.dataNameCreator = dataNameCreator
        this.dataActor = dataActor
        this.dataEmail = dataEmail
        this.dataGender = dataGender
        this.dataCompany = dataCompany
        this.dataAddress = dataAddress

    }
    constructor() {}

}
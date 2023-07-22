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
    var dataPrice: String? = null

    var dataFarmer: String? = null
    var dataDay: String? = null
    var dataPlantingArea: String? = null
    var dataFertilizer: String? = null
    var dataPesticides: String? = null

    var dataDateCreate: String? = null

    var dataNotes: String? = null

    constructor(pid: String?, dataQrCode: String?, dataQrCodeUpdate: String?, dataPicProduct: String?, dataVariety: String?, dataWeight: String?, dataGrade: String?, dataPrice: String?,
                dataFarmer: String?, dataDay: String?, dataPlantingArea: String?, dataFertilizer: String?, dataPesticides: String?, dataDateCreate: String?,
                dataNotes: String?){

        this.pid = pid
        this.dataQrCode = dataQrCode
        this.dataQrCodeUpdate = dataQrCode
        this.dataPicProduct = dataPicProduct
        this.dataVariety = dataVariety
        this.dataWeight = dataWeight
        this.dataGrade = dataGrade
        this.dataPrice = dataPrice

        this.dataFarmer = dataFarmer
        this.dataDay = dataDay
        this.dataPlantingArea = dataPlantingArea
        this.dataFertilizer = dataFertilizer
        this.dataPesticides = dataPesticides

        this.dataDateCreate = dataDateCreate

        this.dataNotes = dataNotes

    }
    constructor() {}

}
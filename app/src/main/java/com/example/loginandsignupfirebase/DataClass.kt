package com.example.loginandsignupfirebase

import com.google.zxing.qrcode.encoder.QRCode

class DataClass {
    var pid: String? = null
    var dataFarmer: String? = null
    var dataVariety: String? = null
    var dataWeight: String? = null
    var dataDate: String? = null
    var dataQrCode: String? = null

    constructor(pid: String?, dataFarmer: String?, dataVariety: String?, dataWeight: String?, dataDate: String?, dataQrCode: String?){
        this.pid = pid
        this.dataFarmer = dataFarmer
        this.dataVariety = dataVariety
        this.dataWeight = dataWeight
        this.dataDate = dataDate
        this.dataQrCode = dataQrCode
    }

    constructor(){

    }
}
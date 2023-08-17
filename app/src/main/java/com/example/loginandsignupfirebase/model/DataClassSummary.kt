package com.example.loginandsignupfirebase.model

class DataClassSummary {
    var dateIn: String? = null
    var variety: String? = null
    var pid: String? = null
    var dateOut: String? = null
    var weightIn: Int? = null
    var purchasePrice: Int? = null
    var handlingFee: Int? = null
    var weightOut: Int? = null
    var sellingPrice: Int? = null
    var purchaseCapital: Int? = null
    var totalHandlingFee: Int? = null
    var capital: Int? = null
    var income: Int? = null
    var profit: Int? = null

    constructor(
        dateIn: String?, variety: String?, pid: String?, dateOut: String?,
        weightIn: Int?, purchasePrice: Int, handlingFee: Int?,
        weightOut: Int, sellingPrice: Int, purchaseCapital: Int, totalHandlingFee: Int,
        capital: Int, income: Int, profit: Int
    )

    {
        this.dateIn = dateIn
        this.variety = variety
        this.pid = pid
        this.dateOut = dateOut
        this.weightIn = weightIn
        this.purchasePrice = purchasePrice
        this.handlingFee = handlingFee
        this.weightOut = weightOut
        this.sellingPrice = sellingPrice
        this.purchaseCapital = purchaseCapital
        this.totalHandlingFee = totalHandlingFee
        this.capital = capital
        this.income = income
        this.profit = profit
    }
    constructor()
}
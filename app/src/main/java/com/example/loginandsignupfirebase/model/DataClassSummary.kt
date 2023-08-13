package com.example.loginandsignupfirebase.model

class DataClassSummary {
    var dateIn: String? = null
    var variety: String? = null
    var pid: String? = null
    var dateOut: String? = null
    var weightIn: Int? = null
    var purchasePrice: Int? = null
    var costPrices: Int? = null
    var weightOut: Int? = null
    var sellingPrice: Int? = null
    var purchaseCapital: Int? = null
    var capitalCosts: Int? = null
    var totalCapital: Int? = null
    var totalSell: Int? = null
    var profit: Int? = null

    constructor(
        dateIn: String?, variety: String?, pid: String?, dateOut: String?,
        weightIn: Int?, purchasePrice: Int, costPrices: Int?,
        weightOut: Int, sellingPrice: Int, purchaseCapital: Int, capitalCosts: Int, totalCapital: Int,
        totalSell: Int, profit: Int
    )

    {
        this.dateIn = dateIn
        this.variety = variety
        this.pid = pid
        this.dateOut = dateOut
        this.weightIn = weightIn
        this.purchasePrice = purchasePrice
        this.costPrices = costPrices
        this.weightOut = weightOut
        this.sellingPrice = sellingPrice
        this.purchaseCapital = purchaseCapital
        this.capitalCosts = capitalCosts
        this.totalCapital = totalCapital
        this.totalSell = totalSell
        this.profit = profit
    }
    constructor()
}
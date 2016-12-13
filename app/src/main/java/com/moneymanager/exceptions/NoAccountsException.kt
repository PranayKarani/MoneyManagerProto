package com.moneymanager.exceptions

// Created by PranayKarani on 12-12-2016.
class NoAccountsException(myMessage: String?) : Exception(myMessage ?: "Account table should not be empty") {

    constructor() : this("Account table should not be empty") {

    }

}

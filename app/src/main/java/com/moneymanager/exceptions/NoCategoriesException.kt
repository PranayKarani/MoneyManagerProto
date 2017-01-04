package com.moneymanager.exceptions

// TODO remove this class if not needed
class NoCategoriesException(myMessage: String?) : Exception(myMessage ?: "No Category f") {

	constructor() : this("Account table should not be empty") {

	}

}
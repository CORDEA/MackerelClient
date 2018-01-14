package jp.cordea.mackerelclient.api.response

class Services(val services: List<Service>)

class Service(val name: String, val memo: String, val roles: Array<String>)

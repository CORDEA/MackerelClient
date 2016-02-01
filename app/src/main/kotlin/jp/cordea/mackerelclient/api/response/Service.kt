package jp.cordea.mackerelclient.api.response

/**
 * Created by CORDEA on 2016/01/11.
 */
class Services(val services: List<Service>)

class Service(val name: String, val memo: String, val roles: Array<String>)

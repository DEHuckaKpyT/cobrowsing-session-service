package com.example.cobrowsing.plugins

import com.netflix.appinfo.ApplicationInfoManager
import com.netflix.appinfo.EurekaInstanceConfig
import com.netflix.appinfo.InstanceInfo
import com.netflix.appinfo.MyDataCenterInstanceConfig
import com.netflix.appinfo.providers.EurekaConfigBasedInstanceInfoProvider
import com.netflix.discovery.DefaultEurekaClientConfig
import com.netflix.discovery.DiscoveryClient
import com.netflix.discovery.EurekaClient
import io.ktor.server.application.*
import io.ktor.server.application.hooks.*


/**
 * Created on 14.01.2023.
 *<p>
 *
 * @author Denis Matytsin
 */
fun Application.configureEurekaClient() {
    install(EurekaClient)
}

val EurekaClient = createApplicationPlugin(name = "eureka-client") {

    application.log.info("Starting eureka-client")
    val instanceConfig: EurekaInstanceConfig = MyDataCenterInstanceConfig()
    val instanceInfo: InstanceInfo = EurekaConfigBasedInstanceInfoProvider(instanceConfig).get()

    val applicationInfoManager: ApplicationInfoManager = ApplicationInfoManager(instanceConfig, instanceInfo)
    val eurekaClient: EurekaClient = DiscoveryClient(applicationInfoManager, DefaultEurekaClientConfig())

    fun startEureka() {
        applicationInfoManager.setInstanceStatus(InstanceInfo.InstanceStatus.UP)
    }

    fun stopEureka() {
        application.log.info("Stopping eureka-client")
        eurekaClient.shutdown()
    }

    on(MonitoringEvent(ApplicationStarted)) { application ->
        startEureka()
        application.log.info("Eureka-client is started")
    }

    on(MonitoringEvent(ApplicationStopped)) { application ->
        stopEureka()
        application.log.info("Eureka-client is stopped")

        // Release resources and unsubscribe from events
        application.environment.monitor.unsubscribe(ApplicationStarted) {}
        application.environment.monitor.unsubscribe(ApplicationStopped) {}
    }
}


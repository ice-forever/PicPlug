plugins {
    val kotlinVersion = "1.8.10"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion

    id("net.mamoe.mirai-console") version "2.16.0"
}

group = "nju.eur3ka"
version = "2.1.2"

repositories {
    mavenLocal()
    maven("https://maven.aliyun.com/repository/public")
    mavenCentral()
}
dependencies{
    implementation("com.twelvemonkeys.imageio:imageio-webp:3.10.1")
}
mirai {
    jvmTarget = JavaVersion.VERSION_1_8
}

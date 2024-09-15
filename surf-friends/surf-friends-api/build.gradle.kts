plugins {
    id("dev.slne.java-common")
    id("dev.slne.java-shadow")
    id("io.freefair.lombok") version "8.10"
}

dependencies {
    compileOnlyApi(libs.paper.api)
}

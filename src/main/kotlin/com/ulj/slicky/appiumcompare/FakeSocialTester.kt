package com.ulj.slicky.appiumcompare

import io.appium.java_client.android.AndroidDriver
import io.appium.java_client.remote.AndroidMobileCapabilityType.APP_ACTIVITY
import io.appium.java_client.remote.AndroidMobileCapabilityType.APP_PACKAGE
import io.appium.java_client.remote.MobileCapabilityType.*
import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import org.openqa.selenium.remote.DesiredCapabilities
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import java.io.File
import java.net.URL
import kotlin.concurrent.thread

/**
 * Created by SlickyPC on 12.6.2017
 */

fun main() {
    val tester = FakeSocialTester()

    val projects = listOf(
            "JavaFakeSocial",
            "KotlinFakeSocial",
            "AnkoFakeSocial"
    )

    repeat(30) {
        projects.forEach {
            tester.testProject(it)
        }
    }
}

class FakeSocialTester {

    fun testProject(projectName: String) {
        printf("\nStarting $projectName")

        val capabilities = DesiredCapabilities().apply {
//            setCapability(DEVICE_NAME, "emulator-5554")
            setCapability(DEVICE_NAME, "21f985ccab0d7ece")
            setCapability(PLATFORM_NAME, "Android")
            setCapability(NEW_COMMAND_TIMEOUT, 120)
            setCapability(AUTOMATION_NAME, "uiautomator2")
            setCapability("autoGrantPermissions", true)
            setCapability(APPIUM_VERSION, "1.7.2")
//            setCapability(FULL_RESET, true)
            setCapability(APP, findApkPath(projectName))
            setCapability(APP_PACKAGE, findAppPackage(projectName))
            setCapability(APP_ACTIVITY, findActivityPackage(projectName))
//            setCapability(APP_WAIT_PACKAGE, "com.ulj.slicky.${projectName.toLowerCase()}")
//            setCapability(APP_WAIT_ACTIVITY, ".activity.login.LoginActivity")
        }

        AndroidDriver<WebElement>(URL("http://0.0.0.0:4723/wd/hub"), capabilities).apply {

            println("DONE (sessionId: $sessionId)")

            analyze(projectName) {
                signUpRoutine()
                signOutRoutine()
                signInRoutine()
                deleteContentRoutine()
                createContentRoutine()
                profileRoutine()
                friendsRoutine()
                settingsRoutine()
                aboutRoutine()
            }

            println("$projectName finished!")
            closeApp()
            quit()
        }
    }

    /**
     * ROUTINES
     */

    private fun <T : WebElement> AndroidDriver<T>.signUpRoutine() = routine("signUpRoutine") {
        waitTillVisible(By.id("signin_signup_button")).click()
        waitTillVisible(By.id("signup_first_name"))
        findElement(By.id("signup_first_name")).sendKeys("First")
        findElement(By.id("signup_last_name")).sendKeys("Last")
        findElement(By.id("signup_email")).sendKeys("first.last@example.com")
        findElement(By.id("signup_first_password")).sendKeys("testtest")
        findElement(By.id("signup_second_password")).sendKeys("testtest")
        findElement(By.id("signup_legal_checkbox")).click()
        waitTillVisible(By.id("signup_signup_button")).click()
    }

    private fun <T : WebElement> AndroidDriver<T>.deleteContentRoutine() = routine("deleteContentRoutine") {
        waitTillVisible(By.id("content_item"))
        findElements(By.id("content_item"))[1].apply { click() }
        waitTillVisible(By.id("action_remove")).click()
        waitTillVisible(By.xpath("//*[@text='YES']")).click()
    }

    private fun <T : WebElement> AndroidDriver<T>.createContentRoutine() = routine("createContentRoutine") {
        waitTillVisible(By.id("action_create")).click()
        waitTillVisible(By.id("creator_text")).sendKeys("Test Content")
        findElement(By.id("creator_button")).click()
        waitTillVisible(By.xpath("//*[@text='COOL!']")).click()
    }

    private fun <T : WebElement> AndroidDriver<T>.profileRoutine() = routine("profileRoutine") {
        waitTillVisible(By.xpath("//android.widget.ImageButton[@content-desc='Open navigation drawer']")).click()
        waitTillVisible(By.xpath("//android.widget.CheckedTextView[@text='Profile']")).click()
        waitTillVisible(By.xpath("//android.widget.ImageButton[@content-desc='Navigate up']")).click()
    }

    private fun <T : WebElement> AndroidDriver<T>.friendsRoutine() = routine("friendsRoutine") {
        waitTillVisible(By.xpath("//android.widget.ImageButton[@content-desc='Open navigation drawer']")).click()
        waitTillVisible(By.xpath("//android.widget.CheckedTextView[@text='Friends']")).click()
        waitTillVisible(By.id("friends_item"))
        findElements(By.id("friends_item"))[2].apply { click() }
        waitTillVisible(By.xpath("//android.widget.ImageButton[@content-desc='Navigate up']")).click()
        waitTillVisible(By.xpath("//android.widget.ImageButton[@content-desc='Navigate up']")).click()
    }

    private fun <T : WebElement> AndroidDriver<T>.settingsRoutine() = routine("settingsRoutine") {
        waitTillVisible(By.xpath("//android.widget.ImageButton[@content-desc='Open navigation drawer']")).click()
        waitTillVisible(By.xpath("//android.widget.CheckedTextView[@text='Settings']")).click()
        waitTillVisible(By.xpath("//android.widget.ImageButton[@content-desc='Navigate up']")).click()
    }

    private fun <T : WebElement> AndroidDriver<T>.aboutRoutine() = routine("aboutRoutine") {
        waitTillVisible(By.xpath("//android.widget.ImageButton[@content-desc='Open navigation drawer']")).click()
        waitTillVisible(By.xpath("//android.widget.CheckedTextView[@text='About']")).click()
        waitTillVisible(By.xpath("//android.widget.ImageButton[@content-desc='Navigate up']")).click()
    }

    private fun <T : WebElement> AndroidDriver<T>.signOutRoutine() = routine("signOutRoutine") {
        waitTillVisible(By.xpath("//android.widget.ImageButton[@content-desc='Open navigation drawer']")).click()
        waitTillVisible(By.xpath("//android.widget.CheckedTextView[@text='Log Out']")).click()
    }

    private fun <T : WebElement> AndroidDriver<T>.signInRoutine() = routine("signInRoutine") {
        waitTillVisible(By.id("signin_email")).apply {
            clear()
            sendKeys("change@me.pls")
        }
        waitTillVisible(By.id("signin_password")).apply {
            clear()
            sendKeys("password")
        }
        waitTillVisible(By.id("signin_signin_button")).click()
        waitTillVisible(By.id("content_item"))
    }

    /**
     * HELPERS
     */

    private fun findApkPath(projectName: String): String {
        return javaClass.getResource("/$projectName/app-appium-proguard.apk").path
    }

    private fun findAppPackage(projectName: String): String {
        return "com.ulj.slicky.${projectName.toLowerCase()}.appium"
    }

    private fun findActivityPackage(projectName: String): String {
        return "com.ulj.slicky.${projectName.toLowerCase()}.activity.content.ContentActivity"
    }

    private fun <T : WebElement> AndroidDriver<T>.waitTillVisible(by: By, timeoutSeconds: Long = 30): WebElement {
        return WebDriverWait(this, timeoutSeconds).until(ExpectedConditions.visibilityOfElementLocated(by))
    }

    private fun <T : WebElement> AndroidDriver<T>.routine(name: String, op: AndroidDriver<T>.() -> Unit) {
        printf("Running $name ")
        op()
        println("DONE [${currentActivity()}]")
    }

    private fun printf(s: String, length: Int = 30) = print("%-${length}s".format(s))

    private fun <T : WebElement> AndroidDriver<T>.analyze(projectName: String, function: (AndroidDriver<T>) -> Unit) {
        val analyze = FakeAnalyzer()
        var success = false

        analyze.start()
        try {
            function(this)
            success = true
        } catch (e: Exception) {
            e.printStackTrace()
        }
        analyze.stop()

        if (success) {
            val cpuFile = File("output-data", "$projectName-cpu-${System.currentTimeMillis()}.csv")
            val memFile = File("output-data", "$projectName-mem-${System.currentTimeMillis()}.csv")
            cpuFile.writer().use { it.write(analyze.cpuData.joinToString(",")) }
            memFile.writer().use { it.write(analyze.memData.joinToString(",")) }
        }
    }

    private class FakeAnalyzer {

        private var shouldRun = false
        private var thread: Thread? = null

        val cpuData = mutableListOf<Double>()
        val memData = mutableListOf<Double>()

        fun start() {
            if (shouldRun) return
            shouldRun = true
            thread = thread {
                println("FakeAnalyzer thread started")
                while (shouldRun) {
                    try {
                        startProcess()
                    } catch (e: Exception) {
                        if (shouldRun) {
                            e.printStackTrace()
                        }
                    }
                }
                println("FakeAnalyzer thread stopped")
            }
        }

        fun stop() {
            shouldRun = false
            thread?.interrupt()
            thread = null
        }

        private fun startProcess() {
            val adbPath = "${System.getProperty("user.home")}/Android/Sdk/platform-tools/adb"
            val adbCommand = "shell top -n 1 -m 20"
            val p = Runtime.getRuntime().exec("$adbPath $adbCommand")
            p.waitFor()
            p.inputStream.reader().use { reader ->
                processLines(reader.readLines())
            }
        }

        private fun processLines(lines: List<String>) {
            val line = lines.firstOrNull { it.contains("com.ulj.slicky") }
            var cpuUsage = -1.0
            var memUsage = -1.0

            if (line != null) {
                try {
                    val cpuMemSubstring = line.substring(42, 54).trim()
                    val cpuMemSplit = cpuMemSubstring.split("\\s+".toRegex())
                    cpuUsage = cpuMemSplit[0].toDouble()
                    memUsage = cpuMemSplit[1].toDouble()
                } catch (e: Exception) {
                    println("Error for line: \"$line\"")
                    e.printStackTrace()
                }
            }

            cpuData += cpuUsage
            memData += memUsage
        }

//        private fun startProcess() {
//            val memoryInfo = getInfo("memoryinfo")
//            memoryInfo.forEach { println(it) }
//            val cpuInfo = getInfo("cpuinfo", 1000)
//            cpuInfo.forEach { println(it) }
//        }

//        private fun getInfo(dataType: String, timeout: Int = 10): HashMap<String, Double> {
//            val data = driver.getPerformanceData("com.ulj.slicky.${projectName.toLowerCase()}.appium", dataType, timeout)
//            val readableData = HashMap<String, Double>()
//            for (i in 0 until data[0].size) {
//                val value = if (data[1][i] == null) {
//                    0.0
//                } else {
//                    (data[1][i] as String).toDouble()
//                }
//                readableData[data[0][i] as String] = value
//            }
//            return readableData
//        }

    }

}

package com.slicky.ulj.appiumcompare

import io.appium.java_client.android.AndroidDriver
import io.appium.java_client.remote.AndroidMobileCapabilityType.*
import io.appium.java_client.remote.MobileCapabilityType.*
import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import org.openqa.selenium.remote.DesiredCapabilities
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import java.net.URL


/**
 * Created by SlickyPC on 12.6.2017
 */
fun main(args: Array<String>) {

    val tester = FakeSocialTester()

    val projects = listOf(
            "JavaFakeSocial",
            "KotlinFakeSocial",
            "AnkoFakeSocial"
    )

    projects.forEach { tester.testProject(it) }

}

class FakeSocialTester {

    fun testProject(projectName: String) {

        println()
        printf("Starting $projectName")

        val capabilities = DesiredCapabilities().apply {
            setCapability(DEVICE_NAME, "emulator-5554")
            setCapability(PLATFORM_NAME, "Android")
            setCapability(NEW_COMMAND_TIMEOUT, "120")
            setCapability(AUTOMATION_NAME, "uiautomator2")
            setCapability(APPIUM_VERSION, "1.6.5")
//            setCapability(FULL_RESET, true)
//            setCapability(AUTO_WEBVIEW, true)
            setCapability(APP, javaClass.getResource("/$projectName/app-release.apk").path)
            setCapability(APP_PACKAGE, "com.ulj.slicky.${projectName.toLowerCase()}")
            setCapability(APP_ACTIVITY, ".activity.content.ContentActivity")
//            setCapability(APP_WAIT_PACKAGE, "com.ulj.slicky.${projectName.toLowerCase()}")
//            setCapability(APP_WAIT_ACTIVITY, ".activity.login.LoginActivity")
        }

        AndroidDriver<WebElement>(URL("http://0.0.0.0:4723/wd/hub"), capabilities).apply {

            println("DONE (sessionId: $sessionId)")

            signUpRoutine()
            signOutRoutine()
            signInRoutine()
            deleteContentRoutine()
            createContentRoutine()
            profileRoutine()
            friendsRoutine()
            settingsRoutine()
            aboutRoutine()

            println("$projectName finished!")
            closeApp()
            quit()
        }
    }

    fun printf(s: String, length: Int = 30) = print("%-${length}s".format(s))

    fun <T : WebElement> AndroidDriver<T>.waitTillVisible(by: By, timeoutSeconds: Long = 15): WebElement {
        return WebDriverWait(this, timeoutSeconds).until(ExpectedConditions.visibilityOfElementLocated(by))
    }

    fun <T : WebElement> AndroidDriver<T>.routine(name: String, op: AndroidDriver<T>.() -> Unit) {
        printf("Running $name ")
        op()
        println("DONE [${currentActivity()}]")
    }

    fun <T : WebElement> AndroidDriver<T>.signUpRoutine() = routine("signUpRoutine") {
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

    fun <T : WebElement> AndroidDriver<T>.deleteContentRoutine() = routine("deleteContentRoutine") {
        waitTillVisible(By.id("content_item"))
        findElements(By.id("content_item"))[1].apply { click() }
        waitTillVisible(By.id("action_remove")).click()
        waitTillVisible(By.xpath("//*[@text='YES']")).click()
    }

    fun <T : WebElement> AndroidDriver<T>.createContentRoutine() = routine("createContentRoutine") {
        waitTillVisible(By.id("action_create")).click()
        waitTillVisible(By.id("creator_text")).sendKeys("Test Content")
        findElement(By.id("creator_button")).click()
        waitTillVisible(By.xpath("//*[@text='COOL!']")).click()
    }

    fun <T : WebElement> AndroidDriver<T>.profileRoutine() = routine("profileRoutine") {
        waitTillVisible(By.xpath("//android.widget.ImageButton[@content-desc='Open navigation drawer']")).click()
        waitTillVisible(By.xpath("//android.widget.CheckedTextView[@text='Profile']")).click()
        waitTillVisible(By.xpath("//android.widget.ImageButton[@content-desc='Navigate up']")).click()
    }

    fun <T : WebElement> AndroidDriver<T>.friendsRoutine() = routine("friendsRoutine") {
        waitTillVisible(By.xpath("//android.widget.ImageButton[@content-desc='Open navigation drawer']")).click()
        waitTillVisible(By.xpath("//android.widget.CheckedTextView[@text='Friends']")).click()
        waitTillVisible(By.id("friends_item"))
        findElements(By.id("friends_item"))[2].apply { click() }
        waitTillVisible(By.xpath("//android.widget.ImageButton[@content-desc='Navigate up']")).click()
        waitTillVisible(By.xpath("//android.widget.ImageButton[@content-desc='Navigate up']")).click()
    }

    fun <T : WebElement> AndroidDriver<T>.settingsRoutine() = routine("settingsRoutine") {
        waitTillVisible(By.xpath("//android.widget.ImageButton[@content-desc='Open navigation drawer']")).click()
        waitTillVisible(By.xpath("//android.widget.CheckedTextView[@text='Settings']")).click()
        waitTillVisible(By.xpath("//android.widget.ImageButton[@content-desc='Navigate up']")).click()
    }

    fun <T : WebElement> AndroidDriver<T>.aboutRoutine() = routine("aboutRoutine") {
        waitTillVisible(By.xpath("//android.widget.ImageButton[@content-desc='Open navigation drawer']")).click()
        waitTillVisible(By.xpath("//android.widget.CheckedTextView[@text='About']")).click()
        waitTillVisible(By.xpath("//android.widget.ImageButton[@content-desc='Navigate up']")).click()
    }

    fun <T : WebElement> AndroidDriver<T>.signOutRoutine() = routine("signOutRoutine") {
        waitTillVisible(By.xpath("//android.widget.ImageButton[@content-desc='Open navigation drawer']")).click()
        waitTillVisible(By.xpath("//android.widget.CheckedTextView[@text='Log Out']")).click()
    }

    fun <T : WebElement> AndroidDriver<T>.signInRoutine() = routine("signInRoutine") {
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
}
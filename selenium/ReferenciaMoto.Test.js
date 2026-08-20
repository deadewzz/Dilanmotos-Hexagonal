const { Builder, By, until } = require('selenium-webdriver');
const chrome = require('selenium-webdriver/chrome');

async function referenciaMotoTest() {
    const options = new chrome.Options();
    options.addArguments('--no-sandbox');
    options.addArguments('--disable-dev-shm-usage');
    options.addArguments('--log-level=3');
    options.addArguments('--silent');
    options.addArguments('--disable-logging');

    let driver = await new Builder()
        .forBrowser('chrome')
        .setChromeOptions(options)
        .build();

    try {
        await driver.get('http://localhost:5173/referencia-moto');

        
        const selectMarca = await driver.wait(
            until.elementLocated(By.name('marca')), 
            15000
        );
        await selectMarca.click();

        
        const optionMarca = await driver.wait(
            until.elementLocated(By.xpath("//select[@name='marca']/option[2]")),
            10000
        );
        await optionMarca.click();

        
        const inputModelo = await driver.findElement(By.name('modelo'));
        await inputModelo.sendKeys('YZF-R3');

        
        const inputCilindraje = await driver.findElement(By.name('cilindraje'));
        await inputCilindraje.sendKeys('321');

        
        const submitButton = await driver.findElement(By.name('submit'));
        await submitButton.click();

        
        await driver.wait(until.alertIsPresent(), 5000);
        const alert = await driver.switchTo().alert();
        await alert.accept();

        console.log('Referencia Moto test passed');
    } catch (error) {
        console.error('Referencia Moto test failed:', error);
    } finally {
        await driver.quit();
    }
}

referenciaMotoTest();
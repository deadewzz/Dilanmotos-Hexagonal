const {Builder, By, until} = require('selenium-webdriver');

(async function loginTest() {
    let driver = await new Builder().forBrowser('chrome').build();
        try {
        await driver.get('http://localhost:5173/login');
        await driver.wait(until.elementLocated(By.name('correo')), 5000);
        await driver.findElement(By.name('correo')).sendKeys('an@gmail.com');
        await driver.findElement(By.name('contrasena')).sendKeys('123');
        await driver.findElement(By.name('IniciarSesión')).click();
        await driver.wait(until.urlIs('http://localhost:5173/dashboard'), 5000);
        console.log('Login test passed');
    }
    catch (error) {
        console.error('Login test failed:', error);
    }
    finally {
        await driver.quit();
    }
})();
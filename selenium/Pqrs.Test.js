const { Builder, By, until } = require('selenium-webdriver');
const chrome = require('selenium-webdriver/chrome');

(async function pqrsTest() {
    const options = new chrome.Options();
    options.addArguments('--log-level=3');
    options.addArguments('--silent');
    options.addArguments('--disable-logging');

    let driver = await new Builder()
        .forBrowser('chrome')
        .setChromeOptions(options)
        .build();

    try {
        const BASE_URL = 'http://localhost:5173';

        // 1. Ir al formulario
        await driver.get(`${BASE_URL}/nueva-pqrs`);

        // 2. Seleccionar Tipo
        const selectElement = await driver.wait(
            until.elementLocated(By.css('select.input-bs')),
            10000
        );
        await selectElement.click();
        const optionElement = await driver.wait(
            until.elementLocated(By.xpath("//option[@value='Peticion']")),
            5000
        );
        await optionElement.click();

        // 3. Llenar Asunto
        const asuntoInput = await driver.wait(
            until.elementLocated(By.xpath("//input[@placeholder='Ej: Problema con repuesto']")),
            5000
        );
        await asuntoInput.clear();
        await asuntoInput.sendKeys('Asunto de prueba automatizada');

        // 4. Llenar Mensaje / Descripción
        const descInput = await driver.wait(
            until.elementLocated(By.css('textarea.input-bs')),
            5000
        );
        await descInput.clear();
        await descInput.sendKeys('Descripción de prueba automatizada para la solicitud de PQRS.');

        // 5. Enviar formulario
        const btnSubmit = await driver.wait(
            until.elementLocated(By.xpath("//button[@type='submit' and contains(., 'Radicar PQRS')]")),
            5000
        );
        await btnSubmit.click();

        // 6. Aceptar la alerta nativa (window.alert)
        await driver.wait(until.alertIsPresent(), 5000);
        const alert = await driver.switchTo().alert();
        console.log('Mensaje de alerta recibido:', await alert.getText());
        await alert.accept(); // Clic en "Aceptar"

        // 7. Confirmar redirección tras cerrar la alerta
        await driver.wait(until.urlContains('localhost:5173/pqrs'), 10000);
        console.log('✔ PQRS test passed!');

    } catch (error) {
        console.error('PQRS test failed:', error.message);
    } finally {
        await driver.quit();
    }
})();
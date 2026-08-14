const { Builder, By, until } = require('selenium-webdriver');

(async function registerTest() {
    let driver = await new Builder().forBrowser('chrome').build();

    try {
        await driver.get('http://localhost:5173/register');

        const correoUnico = `pepito_${Date.now()}@gmail.com`;

        // 1. Llenar inputs directo por su atributo 'name'
        await driver.wait(until.elementLocated(By.name('nombre')), 5000).sendKeys('Pepito Perez');

        await driver.findElement(By.name('correo')).sendKeys(correoUnico);
        await driver.findElement(By.name('contrasena')).sendKeys('123456');


        await driver.wait(
            until.elementLocated(By.xpath("//select[@name='marca']/option[contains(normalize-space(), 'Yamaha')]")),
            5000
        ).click();

        await driver.wait(
            until.elementLocated(By.xpath("//select[@name='modelo']/option[contains(normalize-space(), 'MT-03')]")),
            5000
        ).click();

        await driver.wait(
            until.elementLocated(By.xpath("//select[@name='tipoServicio']/option[contains(normalize-space(), 'Reparacion')]")),
            5000
        ).click();

        // 3. Hacer clic en el botón directo por su atributo name o data-testid
        await driver.findElement(By.name('btn-registro')).click();

        // 4. Verificación
        await driver.wait(until.urlIs('http://localhost:5173/login'), 5000);
                // 1. Hacer clic en el botón de registro
        await driver.findElement(By.css('[data-testid="btn-completar-registro"]')).click();

        // 2. Esperar a que aparezca la alerta nativa de JavaScript
        await driver.wait(until.alertIsPresent(), 5000);

        // 3. Cambiar el foco del driver hacia la alerta
        let alert = await driver.switchTo().alert();

        // 4. (Opcional) Imprimir el texto de la alerta en consola para verificarlo
        let mensaje = await alert.getText();
        console.log("Alerta detectada:", mensaje);

        // 5. Darle clic al botón "Aceptar" de la alerta
        await alert.accept();

        // 6. Esperar la redirección al login
        await driver.wait(until.urlContains('/login'), 5000);
        console.log('¡Prueba de registro completada con éxito!');

    } catch (error) {
        console.error('La prueba falló:', error);
    } finally {
        await driver.quit();
    }
})();
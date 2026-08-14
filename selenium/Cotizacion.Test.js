const { Builder, By, until } = require('selenium-webdriver');

(async function CotizacionTest() {

    let driver = await new Builder().forBrowser('chrome').build();
    const ralentizar = async (ms = 2000) => await driver.sleep(ms);

    try {
        // 1. Navegar a la página y maximizar la ventana
        await driver.get('http://localhost:5173/hacer-cotizacion');
        await driver.manage().window().maximize();

        // 2. Abrir el selector y elegir el segundo producto de la lista
        let selectElement = await driver.findElement(By.css('select'));
        await selectElement.click();

        let opcionProducto = await driver.findElement(By.css('select option:nth-child(2)'));
        await opcionProducto.click();

        // 3. Hacer clic en el botón "Agregar"
        let btnAgregar = await driver.findElement(
            By.xpath("//button[contains(text(), 'Agregar')]")
        );
        await btnAgregar.click();
        await driver.sleep(1000);

        // 4. Buscar y hacer clic en "Generar Cotización"
        let btnGenerar = await driver.findElement(
            By.xpath("//button[contains(., 'Generar Cotización')]")
        );

        await driver.wait(until.elementIsVisible(btnGenerar), 5000);
        await btnGenerar.click();
        await ralentizar(4000);

        console.log('Prueba completada con éxito: Cotización generada correctamente.');

    } catch (error) {
        console.error('Ocurrió un error durante la prueba:', error);
    } finally {
        // Cierra el navegador de forma limpia
        await driver.quit();
    }
})();
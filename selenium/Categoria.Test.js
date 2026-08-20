const { Builder, By, until } = require('selenium-webdriver');

(async function CrearCategoriaTest() {
    // 1. Inicializar el navegador Chrome
    let driver = await new Builder().forBrowser('chrome').build();

    const PAUSA = 2000; 

    try {
        // Maximizar ventana para visibilidad
        await driver.manage().window().maximize();

        // 2. Navegar al formulario de creación de categoría
        await driver.get('http://localhost:5173/crear-categoria'); // Ajusta la URL si es diferente
        await driver.sleep(PAUSA);

        // 3. Llenar el campo "Nombre de la categoría"
        let inputCategoria = await driver.findElement(
            By.css('input[placeholder*="Nombre"], input[type="text"]')
        );
        await inputCategoria.sendKeys('LUBRICANTES Y ACEITES');
        await driver.sleep(PAUSA);

        // 4. Hacer clic en el botón "CREAR CATEGORIA"
        let btnCrear = await driver.findElement(
            By.xpath("//button[contains(translate(text(), 'CREAR CATEGORIA', 'crear categoria'), 'crear categoria')]")
        );
        await driver.wait(until.elementIsVisible(btnCrear), 5000);
        await btnCrear.click();

        await driver.sleep(4000);

        console.log('✅ Prueba de Creación de Categoría realizada con éxito.');

    } catch (error) {
        console.error('❌ Ocurrió un error en la prueba:', error);
    } finally {
        await driver.quit();
    }
})();
const { Builder, By, until } = require('selenium-webdriver');

(async function RegistrarMotoTest() {
    // 1. Inicializar el navegador Chrome
    let driver = await new Builder().forBrowser('chrome').build();
    const PAUSA = 2000; 

    try {
        
        await driver.manage().window().maximize();

        // 2. Navegar al formulario de registro de moto
        await driver.get('http://localhost:5173/registrar-moto'); // Ajusta la ruta si es diferente
        await driver.sleep(PAUSA);

        // 3. Seleccionar "Suzuki" en el desplegable Marca
        let selectMarca = await driver.findElement(By.css('select'));
        await selectMarca.click();
        await driver.sleep(1000);

        // Busca la opción que contenga la palabra "Suzuki"
        let opcionSuzuki = await driver.findElement(
            By.xpath("//select/option[contains(text(), 'Suzuki')]")
        );
        await opcionSuzuki.click();
        await driver.sleep(PAUSA);

        // 4. Llenar el campo "Modelo"
        let inputModelo = await driver.findElement(
            By.css('input[placeholder*="Modelo"], input[name="modelo"]')
        );
        await inputModelo.sendKeys('GSX-R150');
        await driver.sleep(PAUSA);

        // 5. Llenar el campo "Cilindrage"
        let inputCilindrage = await driver.findElement(
            By.css('input[placeholder*="Cilindrage"], input[placeholder*="Cilindrada"], input[name="cilindrage"]')
        );
        await inputCilindrage.sendKeys('150');
        await driver.sleep(PAUSA);

        // 6. Hacer clic en el botón "REGISTRAR"
        let btnRegistrar = await driver.findElement(
            By.xpath("//button[contains(translate(text(), 'REGISTRAR', 'registrar'), 'registrar')]")
        );
        await driver.wait(until.elementIsVisible(btnRegistrar), 5000);
        await btnRegistrar.click();

        await driver.sleep(4000);

        console.log('✅ Prueba de Registro de Moto (Suzuki) realizada con éxito.');

    } catch (error) {
        console.error('❌ Ocurrió un error en la prueba:', error);
    } finally {
        await driver.quit();
    }
})();
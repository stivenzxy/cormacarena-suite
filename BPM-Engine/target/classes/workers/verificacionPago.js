import { Client, Variables, logger } from 'camunda-external-task-client-js'
import chalk from 'chalk';

const config = {
    baseUrl: 'http://localhost:8080/engine-rest',
    asyncResponseTimeout: 10000,
    use: logger,
};
const client = new Client(config);

console.log(chalk.yellow.bold("🔄 Iniciando Worker de verificación de pago..."));

client.subscribe("verificar-pago", { lockDuration: 10000 }, async function({ task, taskService }) {
    try {
        const valorIngresado = task.variables.get("pagoIngresado");
        const costoReal = task.variables.get("valorRealAPagar");

        let pagoValido = valorIngresado >= costoReal;
        let saldoSobrante = pagoValido ? valorIngresado - costoReal : 0;

        const processVariables = new Variables()
            .set("pagoValido", pagoValido)
            .set("saldoSobrante", saldoSobrante);

        console.log(chalk.blue.bold("\n=== Verificación de Pago ==="));
        console.log(`${chalk.cyan("Valor ingresado:")} $${valorIngresado}`);
        console.log(`${chalk.cyan("Valor esperado: ")} $${costoReal}`);

        if (pagoValido) {
            console.log(chalk.green.bold("✅ Resultado: PAGADO"));
            if (saldoSobrante > 0) {
                console.log(chalk.green(`💰 Saldo sobrante: $${saldoSobrante}`));
            }
        } else {
            console.log(chalk.red.bold("❌ Resultado: RECHAZADO"));
            console.log(chalk.red("Monto insuficiente!"));
        }

        await taskService.complete(task, processVariables);

    } catch (error) {
        console.error(chalk.red("⚠️ Error en la tarea de verificación de pago:"), error.message);
        await taskService.handleFailure(task, {
            errorMessage: "Error en verificación de pago",
            errorDetails: error.message,
            retries: 0,
            retryTimeout: 1000
        });
    }
});

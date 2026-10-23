package pe.edu.cibertec.tambo.catalogo.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String EXCHANGE = "tambo.exchange";

    public static final String RK_VENTA_REGISTRADA = "venta.registrada";
    public static final String RK_STOCK_BAJO = "stock.bajo";
    public static final String RK_VENTA_PROCESADA = "venta.procesada";

    public static final String COLA_VENTA_REGISTRADA = "catalogo.venta.registrada";
    public static final String COLA_STOCK_BAJO = "alertas.stock.bajo";
    public static final String COLA_VENTA_PROCESADA = "ventas.venta.procesada";

    @Bean
    Declarables tamboDeclarables() {
        DirectExchange exchange = new DirectExchange(EXCHANGE, true, false);

        Queue ventaRegistrada = new Queue(COLA_VENTA_REGISTRADA, true);
        Queue stockBajo = new Queue(COLA_STOCK_BAJO, true);
        Queue ventaProcesada = new Queue(COLA_VENTA_PROCESADA, true);

        Binding b1 = BindingBuilder.bind(ventaRegistrada).to(exchange).with(RK_VENTA_REGISTRADA);
        Binding b2 = BindingBuilder.bind(stockBajo).to(exchange).with(RK_STOCK_BAJO);
        Binding b3 = BindingBuilder.bind(ventaProcesada).to(exchange).with(RK_VENTA_PROCESADA);

        return new Declarables(exchange, ventaRegistrada, stockBajo, ventaProcesada, b1, b2, b3);
    }
}

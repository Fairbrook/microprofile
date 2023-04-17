# Tolerancia a fallas con MicroProfile, Quarkus

**Autor:** Kevin Alan Martínez Virgen

**Materia:** Computación tolerante a falla

## Índice
1. [Investigación previa](#investigacion)
    1. [¿Qué es JavaSE?](#javase)
    2. [¿Qué es JavaEE?](#javaee)
    3. [¿Qué es JakartaEE?](#jakarta)
    4. [¿Qué es MicroProfile?](#microprofile)
    5. [¿Qué es SpringBoot?](#springboot)
    6. [¿Qué es Quarkus?](#quarkus)
    7. [¿Qué es Maven?](#maven)
    8. [¿Qué es Gradle?](#gradle)
    9. [Fuentes](#fuentes)
2. [Ejemplo](#ejemplo)

## Investigación previa<a name="investigacion"></a>

### ¿Qué es JavaSE?<a name="javase"></a>

Java Platform, Standard Edition (Java SE) es una especificación que describe 
una plataforma Java de resumen. Proporciona una base para crear y desplegar 
aplicaciones que van desde un ordenador de escritorio PC a un servidor de 
grupo de trabajo. 
Java SE lo implementa el kit de desarrollo de software (SDK) Java.

### ¿Qué es JavaEE?<a name="javaee"></a>
Java Platform, Enterprise Edition (Java EE) se basa en la especificación Java SE.
Es un entorno independiente de la plataforma centrado en Java para desarrollar,
crear e implementar en línea aplicaciones empresariales basadas en web. 

La plataforma Java EE consta de un conjunto de servicios, API y protocolos que
proporcionan la funcionalidad necesaria para desarrollar aplicaciones basadas 
en web de varios niveles.

Java EE simplifica el desarrollo de aplicaciones y reduce la necesidad de 
programación y formación para programadores al crear componentes modulares 
normalizados y reutilizables, así como al permitir controlar muchos aspectos 
de la programación automáticamente por nivel. 

### ¿Qué es JakartaEE?<a name="jakarta"></a>
Es un estandar que define un conjunto de especificaciones para el desarrollo
de aplicaciones de negocio de Java. Que actua como continuación y estandarización
de la plataforma JavaEE enfocada en modernizar el ambiente de desarrollo para 
las arcquitecturas en la nube

### ¿Qué es MicroProfile?<a name="microprofile"></a>
Es un projecto que contiene múltiples API's a forma de bloques para diferentes
soluciones basadas en una arquitectura de microservicios. Permite a los 
desarrolladores de JavaEE utilizar su enfoque tradicional de 3 capas para construir 
sistemas de microservicios

### ¿Qué es SpringBoot?<a name="springboot"></a>
Sping boot es un framwork de desarrollo para Java de códiogo avierto que permite
principalmente la creación de aplicaciones back-end, facilita la interoperatibilidad,
simplifica las dependencias y simplifica la configuración

### ¿Qué es Quarkus?<a name="quarkus"></a>
Es un framework de Java nativo de Kubernetes adaptado a GraalVM y HotSpot. Con el
objetivo de convertir a Java en la plataforma líder en Kubernetes y entornos sin
servidor al mismo tiemo que ofrece una grama amplia de arquitecturas para
aplicaciones distribuidas

### ¿Qué es Maven?<a name="maven"></a>
Es una herramienta de administración de proyectos de software basado en el concepto
de modelo de objeto de proyecto, Mave puede administrar la compilación del projecto,
reportes y documentación desde una pieza central de informacioñ

### ¿Qué es Gradle?<a name="gradle"></a>
Es un paquete de herramientas de compilación avanzadas para automatizar y administrar
el proceso de compilación y, al mismo tiempo, definir configuraciones de compilación
personalizadas y flexibles

### Fuentes<a name="fuentes"></a>
- https://www.ibm.com/docs/es/odm/8.5.1?topic=application-java-se-java-ee-applications
- https://www.java.com/es/download/help/techinfo.html
- https://jakarta.ee/about/why-jakarta-ee/
- https://microprofile.io/resources/#white-paper
- https://www.tokioschool.com/noticias/spring-boot/
- https://es.quarkus.io/about/
- https://developer.android.com/studio/build?hl=es-419

## Ejemplo<a name="ejemplo"></a>
En el ejemplo se utilizará Quarkus para hacer un endpoint que contenga Tolerancia
a fallos mediante la configuración de métricas configurables

Primero se deben seleccionar la librerías que se utilizarán en el proyecto mediante
la página de Quarkus, y descargar el zip generado

![quarkus](https://i.ibb.co/LZhvBN4/2023-04-16-232945-3520x1080-scrot.png)

Luego podemos proceder a hacer la implementación del endpoint deseado, en este
caso un solo endpoint que regresa una lista de personas

```java
@Path("/persons")
@Produces(MediaType.APPLICATION_JSON)
public class PersonController {
    List<Person> personList = new ArrayList<Person>();
    Logger LOGGER = Logger.getLogger("DemoLogger");

    @GET
    public List<Person> getPersonList() {
        return personList;
    }
}
```

Una vez configurado el endpoint simple podemos agregar un decorador para ejecutar
una función en caso de fallo y para probar podemos agregar una función que 
genera una Exception de forma aleatoria

```java
@Path("/persons")
@Produces(MediaType.APPLICATION_JSON)
public class PersonController {
    List<Person> personList = new ArrayList<Person>();
    Logger LOGGER = Logger.getLogger("DemoLogger");

    @GET
    @Fallback(fallbackMethod = "getPersonFallbackList")
    public List<Person> getPersonList() {
        doFail();
        //doWait();
        return personList;
    }

    public List<Person> getPersonFallbackList(){
        var person = new Person(-1L, "Kevin", "kevinvr@hotmail.es");
        return List.of(person);
    }

    public void doFail(){
        var random = new Random();
        if(random.nextBoolean()){
            LOGGER.warning("Falla producida");
            throw new RuntimeException("Haciendo qu ela implementación falle");
        }
    }
}
```
Aunado al decorador Fallback, tambien podemos utilizar el decorador Retry y Timeout
que permite especificar reitentos y tiempo máximo de espera

Como parte final de la configuración del endpoint agregamos los decoradores 
CircuitBreaker y BulkHead que permiten implementar un número máximo de conexiones
y un número máximo de fallas

```java
@Path("/persons")
@Produces(MediaType.APPLICATION_JSON)
public class PersonController {
    List<Person> personList = new ArrayList<Person>();
    Logger LOGGER = Logger.getLogger("DemoLogger");

    @GET
    @Timeout(value = 5000L)
    @Retry(maxRetries = 3)
    @CircuitBreaker(failureRatio = 0.1, delay = 15000L)
    @Bulkhead(value = 1)
    @Fallback(fallbackMethod = "getPersonFallbackList")
    public List<Person> getPersonList() {
        doFail();
        //doWait();
        return personList;
    }

    public List<Person> getPersonFallbackList(){
        var person = new Person(-1L, "Kevin", "kevinvr@hotmail.es");
        return List.of(person);
    }

    public void doWait(){
        var random = new Random();
        try{
            LOGGER.warning("Haciendo un sleep");
            Thread.sleep(random.nextInt(10)+1*1000L);
        }catch (Exception e){

        }
    }
    public void doFail(){
        var random = new Random();
        if(random.nextBoolean()){
            LOGGER.warning("Falla producida");
            throw new RuntimeException("Haciendo qu ela implementación falle");
        }
    }
}
```

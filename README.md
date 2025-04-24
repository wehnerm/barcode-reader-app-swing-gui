# Barcode Reader Application

This is a Java Swing-based application that allows users to paste an image from the clipboard, display the image, and decode the barcode or QR code content using the ZXing library. The decoded content is displayed below the image.

![Barcode Reader Screenshot](example-with-qr-code.png)

## Prerequisites

- Java 8 or higher
- Maven 3.6 or higher

## How to Run

### From source code

1. Clone or download the project to your local machine.
2. Navigate to the project directory in your terminal.
3. Compile the project using Maven:
   ```bash
   mvn clean package
    ```
4. Run the application:
   ```bash
   java -jar target/barcode-reader-app-0.1.0.jar
    ```
   
## Debugging

To see the content of the jar file, you can use the following command:
```bash
jar tf target/barcode-reader-app-0.1.0.jar
```
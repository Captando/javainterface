# Calculadora Científica em Java

Aplicação Swing com operações básicas, funções científicas e visualização gráfica pensada para rodar em desktops Linux/WSL.

## Recursos
- Operações padrão: soma, subtração, multiplicação, divisão, porcentagem e troca de sinal.
- Funções científicas: potências (`x²`, `xʸ`), exponencial, logaritmos (`log` e `ln`), trigonometria com alternância DEG/RAD, inverso (`1/x`) e raiz quadrada.
- Constantes rápidas de π e `e`, botões AC/CE e suporte a números decimais.
- Painel de gráficos dedicado com funções pré-configuradas (sen, cos, tan, log, ln, `e^x`, `x²`, `x³`) traçadas em um intervalo fixo.

## Requisitos
- JDK 21 (ou compatível com `jpackage`) instalado e configurado no `PATH`.
- Bash/PowerShell para executar o Gradle Wrapper.

## Execução rápida (modo desenvolvimento)
```bash
./gradlew run
```
O Gradle compila o projeto em `src/main/java` e abre a interface Swing. Ao clicar em **Gráfico**, uma nova janela abre com o painel de funções.

## Gerando executável/instalador
O projeto usa o plugin `org.beryx.jlink` para criar imagens auto‑contidas e instaladores assinados como “Captando” versão `0.0.1`.

1. **Imagem executável local (todos os sistemas)**
   ```bash
   ./gradlew jlink
   ./build/image/bin/CalculadoraCaptando
   ```
   Gera um runtime customizado com o aplicativo pronto para distribuir como pasta.

2. **Instalador / `.exe` (executar no sistema-alvo)**
   - Windows:  
     ```powershell
     .\gradlew.bat jpackage -PinstallerType=exe
     ```  
   Produz `build/jpackage/CaptandoCalculator-0.0.1.exe` com atalho de menu.
     > Observação: o `.exe` **precisa** ser gerado a partir do Windows, com o projeto em um caminho local (por exemplo `C:\Users\...\javainterface`). O `jpackage` não aceita caminhos UNC/WSL (`\\wsl.localhost\...`) e não consegue produzir `.exe` quando rodado no Linux.
   - Linux (Debian/Ubuntu):  
     ```bash
     ./gradlew jpackage -PinstallerType=deb
     ```  
     Produz `.deb` instalável em `build/jpackage/`.
   - macOS:  
     ```bash
     ./gradlew jpackage -PinstallerType=pkg
     ```
   É necessário rodar cada comando na respectiva plataforma porque o `jpackage` gera instaladores nativos específicos.

## Estrutura
- `src/main/java/com/captando/calculator/Calculator.java`: interface principal, operações científicas, controle DEG/RAD e janela de gráficos (modularizada como `com.captando.calculator`).
- `src/main/java/module-info.java`: declara o módulo Java (`requires java.desktop` / `exports com.captando.calculator`), necessário para o `jlink`/`jpackage`.
- `build.gradle`: configuração do aplicativo, metadados “Por Captando v0.0.1” e tarefas `jlink`/`jpackage`.
- `gradlew`, `gradlew.bat`: wrappers que baixam automaticamente o Gradle 8.10.2.

Sinta-se à vontade para ampliar a lista de funções, ajustar estilos ou incluir novos alvos no `jpackage`.

# Calculadora Científica em Java

Aplicação Swing com operações básicas, funções científicas e visualização gráfica pensada para rodar em desktops Linux/WSL.

## Recursos
- Operações padrão: soma, subtração, multiplicação, divisão, porcentagem e troca de sinal.
- Funções científicas: potências (`x²`, `xʸ`), exponencial, logaritmos (`log` e `ln`), trigonometria com alternância DEG/RAD, inverso (`1/x`) e raiz quadrada.
- Constantes rápidas de π e `e`, botões AC/CE e suporte a números decimais.
- Painel de gráficos dedicado com funções pré-configuradas (sen, cos, tan, log, ln, `e^x`, `x²`, `x³`) traçadas em um intervalo fixo.

## Requisitos
- Java 17+ (testado com OpenJDK 21 em Ubuntu/WSL).

## Como executar
```bash
javac Calculator.java
java Calculator
```

Ao clicar em **Gráfico**, uma nova janela abrirá. Use o seletor superior para alternar entre as funções disponíveis; o painel redesenha automaticamente.

## Estrutura
- `Calculator.java`: interface principal, operações científicas, controle DEG/RAD e janela de gráficos.
- `Calculator$GraphFrame`, `Calculator$GraphPanel`: classes internas responsáveis pela escolha de funções e renderização dos gráficos.

Sinta-se à vontade para ampliar a lista de funções ou adaptar o painel de gráficos para aceitar expressões personalizadas.

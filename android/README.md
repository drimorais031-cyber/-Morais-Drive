# Morais Drive para Android

Projeto Android nativo separado da PWA publicada na raiz do repositório. A versão web/GPS existente permanece preservada.

## Compatibilidade planejada
- Android 6.0 (API 23) ou superior.
- Aparelhos sem telefonia/SIM ainda podem abrir o app e usar recursos compatíveis, mas não realizam chamadas celulares.
- A seleção como telefone padrão usa RoleManager no Android 10+ e o fluxo legado de TelecomManager no Android 6 a 9.
- Android Auto requer implementação própria com Android for Cars App Library, categoria aprovada e validação específica. Não é espelhamento irrestrito da tela.

## Compilar
1. Abra a pasta `android/` no Android Studio.
2. Use JDK 17 e Android SDK 35.
3. Aguarde a sincronização do Gradle.
4. Execute a configuração `app` em um aparelho ou emulador.

## Implementado nesta base
- Projeto Android Kotlin.
- Discador básico.
- Pedido para tornar o app telefone padrão, com fluxo para Android antigo e novo.
- Serviço InCallService.
- Tela de chamada com atender, recusar/encerrar e alternância de alto-falante.

## Ainda precisa de validação e desenvolvimento
- Build automatizado e teste em aparelhos reais de diferentes fabricantes/versões.
- Tratamento completo de chamadas múltiplas, espera, conferência, Bluetooth e áudio.
- Permissões e experiência de contatos/histórico.
- UI completa para chamada em segundo plano e tela bloqueada em todos os fabricantes.
- Android Auto com biblioteca oficial e testes em carro.
- Integração da navegação GPS dentro do app nativo.

Esta é uma base de desenvolvimento, não um discador final aprovado para distribuição. Recursos e disponibilidade dependem do aparelho, versão Android, operadora e permissões concedidas.

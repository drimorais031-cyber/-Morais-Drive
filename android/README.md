# Morais Drive para Android (nativo)

Este projeto é separado da PWA publicada na raiz do repositório. Não altera a versão web/GPS existente.

## Abrir e compilar
1. Instale o Android Studio.
2. Abra a pasta `android/` como projeto.
3. Aguarde a sincronização do Gradle.
4. Use JDK 17 e Android SDK 35.
5. Execute em um aparelho Android com chip/SIM para testar telefonia.

## Estado desta primeira base
- Projeto Android Kotlin criado.
- Tela inicial com discador básico.
- Solicitação da função de aplicativo de telefone padrão via RoleManager.
- Declaração do InCallService e tela inicial de chamada.
- Chamadas reais dependem de aparelho compatível, SIM, permissões e de o usuário escolher o app como telefone padrão.

## Ainda não concluído
- Teste de build em Android Studio/CI.
- UI completa e robusta para chamadas recebidas/em andamento, áudio, espera, múltiplas chamadas e histórico.
- Leitura e busca de contatos com fluxo de permissões.
- Integração Android Auto com Android for Cars App Library e revisão de elegibilidade.
- Integração do GPS/web dentro do aplicativo nativo.

**Não distribuir esta versão como discador final ainda.** Esta é a estrutura inicial de desenvolvimento, não uma versão pronta para uso diário nem um APK assinado.

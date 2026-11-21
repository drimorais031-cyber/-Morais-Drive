# Morais Drive para Android

Projeto Android nativo separado da PWA publicada na raiz do repositório. A versão web/GPS existente permanece preservada.

## Compatibilidade planejada
- Android 6.0 (API 23) ou superior.
- Aparelhos sem telefonia/SIM podem abrir o app, mas não realizam chamadas celulares.
- A seleção como telefone padrão usa RoleManager no Android 10+ e o fluxo legado de TelecomManager no Android 6 a 9.
- Integração inicial com Android Auto usando Android for Cars App Library 1.7.0.
- O serviço de navegação e a tela de integração estão registrados no manifesto. A tela atual recebe o destino e oferece encaminhamento para um app de mapas compatível.
- A integração ainda NÃO desenha o mapa/rota próprios do Morais Drive na tela do carro nem oferece instruções curva a curva próprias. Isso exige implementar a superfície de mapa, sincronização de rota/localização e testes no Desktop Head Unit e em veículo.
- Android Auto não permite espelhamento irrestrito da tela do celular. Usa modelos aprovados e exige revisão/qualidade do Google para distribuição.

## Compilar
1. Abra a pasta `android/` no Android Studio.
2. Use JDK 17 e Android SDK 35.
3. Aguarde a sincronização do Gradle.
4. Execute a configuração `app` em um aparelho ou emulador.

## Implementado nesta base
- Projeto Android Kotlin.
- Discador básico com pedido de telefone padrão.
- Solicitação de permissão de chamada em tempo de execução.
- Serviço InCallService.
- Tela de chamada com atender, recusar/encerrar e alternância de alto-falante.
- Serviço Android Auto registrado como aplicativo da categoria navegação.
- Tela Android Auto inicial com recepção de destino e encaminhamento para outro app de mapas.
- Build automatizado de APK de depuração via GitHub Actions.

## Ainda precisa de validação e desenvolvimento
- Testes em aparelhos reais de diferentes fabricantes e versões.
- Tratamento completo de chamadas múltiplas, espera, conferência, Bluetooth e áudio.
- Histórico e contatos, caso sejam adicionados.
- Notificação de chamada recebida e tela de chamada em segundo plano compatíveis com as restrições de versões recentes do Android.
- Mapa próprio na superfície do Android Auto, sincronização de destino/rota/localização, instruções de navegação, áudio e teste com Desktop Head Unit/carro.
- Integração da navegação GPS dentro da interface nativa.
- Revisão de qualidade e aprovação do Google Play para distribuição como app de navegação.

Esta é uma base de desenvolvimento, não um discador ou navegador final aprovado para distribuição. Recursos e disponibilidade dependem do aparelho, versão Android, operadora, permissões e aprovação da plataforma.

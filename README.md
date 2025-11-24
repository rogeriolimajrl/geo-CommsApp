# Geo-Comms App – Android (Kotlin)

Aplicativo Android que combina Mapbox, Reconhecimento de voz e busca por locais reais usando a API de Geocoding.
O usuário fala algo como "escola", e o app marca no mapa todas as escolas próximas à localização atual. O App tem também um chat em tempo real.

## Funcionalidades

- Mapa interativo usando Mapbox Maps SDK

- Busca por voz integrada com SpeechRecognizer

- Busca real de lugares com Mapbox Geocoding API

- Marcadores automáticos no mapa conforme o termo falado

- Obtenção da localização real (GPS) do usuário

- Permissões implementadas (location + áudio)

- Chat interno (texto ou voz)

- Botões para alternar entre mapa e chat

- Estruturado com Retrofit e boas práticas

## Tecnologias Utilizadas

- Kotlin: Linguagem principal
- Mapbox Maps SDK: Exibir e manipular mapa
- Mapbox Geocoding API: Buscar lugares reais
- Retrofit2 + Gson: Chamar API de geocodificação
- SpeechRecognizer: Capturar voz
- FusedLocationProviderClient: Pegar localização real



## Dependências (build.gradle)

// Mapbox
implementation("com.mapbox.maps:android:11.4.0")


// Retrofit
implementation("com.squareup.retrofit2:retrofit:2.11.0")
implementation("com.squareup.retrofit2:converter-gson:2.11.0")


## Configurando o Mapbox Token

Use seu token público:

Crie o arquivo local.properties:

MAPBOX_PUBLIC_TOKEN=seu_token_publico_aqui


## AndroidManifest.xml:

- android.permission.INTERNET
- android.permission.RECORD_AUDIO
- android.permission.WRITE_EXTERNAL_STORAGE
- android.permission.READ_EXTERNAL_STORAGE
- android.permission.ACCESS_FINE_LOCATION
- android.permission.ACCESS_COARSE_LOCATION


## Como Rodar o Projeto

Baixe o repositório:

- git clone https://github.com/rogeriolimajrl/geo-CommsApp.git

- Abra no Android Studio (A versão que usei foi o Otter)

- Adicione seu token do Mapbox em local.properties

- Conecte um aparelho físico (GPS funciona melhor)

- Rode o app!
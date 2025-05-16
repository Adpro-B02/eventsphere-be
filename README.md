Link deployment: http://ec2-52-86-194-154.compute-1.amazonaws.com:8080/

# Architecture

## Pre-context Diagram
![Pre-context Diagram](resources/diagram/assets/Pre-context%20diagram.drawio.png)
## Pre-container Diagram
![Pre-container Diagram](resources/diagram/assets/Pre-container%20diagram.drawio.png)
## Pre-deployment Diagram
![Pre-deployment Diagram](resources/diagram/assets/Pre-deployment%20diagram.drawio.png)
## Post-context Diagram
![Post-context Diagram](resources/diagram/assets/Post-context%20diagram.drawio.png)
## Post-container Diagram
![Post-container Diagram](resources/diagram/assets/Post-container%20diagram.drawio.png)

## Risk Storming

Kami menerapkan teknik risk storming secara kolaboratif untuk mengidentifikasi dan memprioritaskan risiko utama. Risiko dengan dampak terbesar yang ditemukan adalah terkait celah keamanan akibat ketiadaan autentikasi dan otorisasi, serta rendahnya kemampuan observabilitas tanpa sistem monitoring terpusat. Tanpa mekanisme autentikasi yang kuat, sistem rentan terhadap akses tidak sah, sedangkan tanpa monitoring, tim akan kesulitan mendiagnosis dan merespons insiden secara cepat di lingkungan cloud-native.

Untuk mengatasi risiko ini, kami memutuskan untuk menambahkan modul autentikasi yang memastikan setiap endpoint hanya dapat diakses oleh pengguna sah, serta mengintegrasikan solusi monitoring terpusat pada fase pengembangan selanjutnya. Komitmen terhadap dua aspek ini akan memberikan fondasi keamanan dan keandalan yang lebih baik, sehingga arsitektur backend Eventsphere siap untuk berkembang secara aman dan dapat diobservasi dengan baik seiring peningkatan jumlah pengguna dan kompleksitas aplikasi.

### Event
#### Code
![Code diagram event](resources/diagram/assets/event.png)

### Ticket
#### Component
![Ticket Component Diagram](resources/diagram/assets/Ticket_Component_Diagram.png)

#### Code
![Ticket Code Diagram](resources/diagram/assets/Ticket_Code_Diagram.png)

### Review
#### Component
![Component diagram review](resources/diagram/assets/review_comp.png)

#### Code
![Code diagram review](resources/diagram/assets/review_code.png)

### Promo
#### Component
![Promo Component Diagram](resources/diagram/assets/promo_component_diagram.png)

#### Code
![Promo Code Diagram](resources/diagram/assets/promo_code_diagram.png)
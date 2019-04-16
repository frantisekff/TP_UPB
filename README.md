# TP_UPB - Tímový projekt Úvod do bezpečnosti
> Aplikácia vytvorená k bezpečnému zdieľaniu súborov medzi zaregistrovanými užívateľmi. 
> Každý registrovaný užívateľ má vygenerovaný súkromný a verejný kľuč, ktoré sa používajú pri šifrovaní/dešifrovaní tajného kľúča.
> Súkromný kľuč je využívaný pri šifrovaní samotného súboru.

> Aplikácia bola priebežne testovaná na bezpečnostne diery pomocou nástrojov  OWASP Zed Attack Proxy, Nikto, Acunetix. Následne bol vytvorený report o bezpečnosti podľa OWASP Top 10 2017

## Použite technológie 
* [Tomcat]
* [JavaServer Pages (JSP)]
* [Java]
* [MySQL]

## Štandardy použite pri šifrovaní
* [AES/CBC] Symetrické šifrovanie 
* [RSA] Asymetrické šifrovanie
* [PBKDF2] Hashovanie hesla

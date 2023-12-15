<h1>EventApi</h1>

<h2>Aplikace má za úkol splnit následující zadání:</h2>

- Na rest endpointu přijme CSV soubor, který má formát Eventů (jeden CSV je přiložen, byl použit při live codingu)
- Příchozí CSV mohou obsahovat data pro libovolné časové úseky
- Zajímají nás pouze url /legislativa/CR[0-9]+.*
- CSV vhodně zpracuje a uloží do PostgreSQL databáze
- Na restu poskytne endpoint ve tvaru zhruba /uniqueUsers/{docId}/{YYYY-mm-dd}/{YYYY-mm-dd} - tj. počet unikátních uživatelů pro dané docId (CRXXXX) mezi 2 dny
 

- Výstup je json se sadou dvojic datum (den) -> počet unikátních uživatelů.

<h2>Testování</h2>

Pro důkaz funkčnosti aplikace napište integrační s nastavením @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT).
V testu použijeme testcontainer pro vytvoření postgresu, zpracuje vzorové CSV a získá výstup pro některý týden v listopadu 2023 a dokument CR10.

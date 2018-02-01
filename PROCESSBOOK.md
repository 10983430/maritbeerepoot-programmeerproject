# Process book

## Week 1

### Maandag 8 januari 2018  
- Werken aan propasal  

### Dinsdag 9 januari 2018  
- Werken aan design document  

### Woensdag 10 januari 2018
- Project opgezet in android studio
- Data uit de APIs gehaald (nog zonder te strippen)
- Firebase geconnect
- Design document afgemaakt
- Begonnen aan login & register

### Donderdag 11 januari 2018
- Login en register mogelijk
- Firebase opgezet
- Listview series overzicht gemaakt
- Bottom navigation gemaakt
- Fragments toegevoegd
- Begonnen aan fragments voor de episode details, user details, serie details
- Prototype waardige app gemaakt door navigatie te fixen en data in te laden
- **Keuze:** Ik heb er voor gekozen om met fragments te werken zodat bottom navigatie mogelijk is.

### Vrijdag 12 januari 2018
- Vandaag niet veel meteen zichtbare veranderingen, maar idee en problemen uit prototype goed geanalyseerd
- Layout veranderingen
- Firebase structuur aangepast na feedback presentaties
- Nagedacht en plan gemaakt over hoe het mogelijk wordt om mensen te volgen
- **Keuze:** Ik heb ervoor gekozen om 'vrienden' te kunnen volgen in plaats van als vriend te kunnen toevoegen op basis van een verzoek. Hier is voor gekozen zodat er in theorie bijvoorbeeld ook bekende mensen kunnen worden gevolgd, zonder dat deze persoon iedereen moet terug volgen.

### Zondag 14 januari 2018
- Login bug gefixt
- Bug gefixt dat het registerfragment en het login fragment 'achter elkaar' komen in plaats van 1 van de twee
- Bug gefixt dat niet alle informatie in de listview komt
- Episode Details pagina gemaakt, dus data opgehaald, geparsed en in een fragment geplaatst
- Begonnen aan user database fragment en informatie uit Firebase halen
- Begonnen aan expandable listview
- Plan van aanpak voor komende week gemaakt

## Week 2

### Maandag 15 januari 2018
- User database gemaakt aan de hand van usernames uit Firebase
- Data API uitgelezen en begonnen met parsen voor de expandable list view van de season nummers en episode nummers
- Toestenbord bug gefixt bij de listview zoek functie
- Bug gefixt dat de listview weer clickable is na het terug gaan naar het fragment
- Opmaak row layout
- **Keuze**: Ik heb er voor gekozen om de episode nummers en seizoenen samen in een expandable listview te zetten. Dit zodat er niet nog een aparte seizoen fragment hoeft te zijn.

### Dinsdag 16 januari 2018
- Eindelijk voor elkaar gekregen om alle JSON data uit te lezen in classen en te zorgen dat automatisch alle informatie van elke seizoenen opgehaald wordt zonder elke seizoen url te hardcoden.
- Expandable listview werkend gekregen. Echter nog niet met de goede data, seizoenen er wel al ingekregen, episodes nog niet. 


![](doc/ProcessExpandablelistview1.png) 

### Woensdag 17 januari 2018
- Vandaag de hele dag bezig geweest met informatie in het goede format de expandable listview in krijgen. Dit lukte helaas niet heel goed, ik ben tegen heel veel bugs aangelopen. Momenteel heb ik de data in een Arraylist met class objecten gekregen. 

### Donderdag 18 januari 2018
- **Opmerking:** Ik heb helaas vandaag niet veel kunnen doen. Nadat het dak van de UvA afvloog heb ik er door de storm ongeveer 6.5 uur over gedaan om thuis te komen (ik was 18.00 pas thuis). Als je denkt dat de NS niet doet aan treinen laten aansluiten op elkaar heb je nog nooit met connexxion gereisd..
- Kleine aanpassingen gedaan aan de serie overview pagina
- Helaas tegen een bugg aangelopen dat de Expandable Listview niet altijd laadt

### Vrijdag 19 januari 2018
- Helaas nogsteeds de expandable listview niet helemaal werkend gekregen. Loop ook tegen een bug aan dat de expandable listview niet tevoorschijn komt. Echter is deze wel te zien wanneer er naar een andere fragment wordt gegaan en vervolgens weer terug naar de serie details. Hard aan het werk aan dit te fixen.
- Veel moeite gedaan om de hashmap werkend te krijgen, maar het is nog niet helemaal gelukt. Het gaat met baby stapjes.

### Zaterdag 20 januari 2018
- Hashmap met data voor expandable listview gefixt
- Data in expandable listview weergeven
- **Keuze:** Elke episode heeft een imdbrating, maar bij sommige is de waarde "N/A". Er is hierom gekozen om hier toch maar een String van te maken in plaats van een double, aangezien er geen berekeningen met de rating zullen worden uitgevoerd.
- Bug gefixt dat de expandable listview niet altijd tevoorschijn komt
- Checkboxes aan de expandable listview toegevoegt
- Expandable listview is clickable, er kan dus op een item in de listview geclickt worden en naar de episode details gegaan worden
- **Problemen:** Hoe kan je zien in een expandable listview of er op een checkbox geklikt is?
- Informatie over episodes die gezien zijn in Firebase gekregen   
![](doc/Screenshot_1516462803.png)

## Week 3

### Maandag 22 januari 2018
- Vandaag liep ik helaas tegen een database probleem aan. Wanneer er een season wordt geinput met de format "1", denkt Firebase dat dit deel uitmaakt van de arraylist en er dus een 0 mist (aangezien firebase zelf nummers gebruikt om waardes van arraylisten te weergeven in de webversie).
Ik heb er hierom voor gekozen om seizoenen in Firebase te zetten met "S-[seizoennummer]" en "E-[episodenummer] (bijv. E-22 voor episode 22). Deze waarde kan dan met een regex split worden gescheiden van de letter die ervoor staat en daardoor later worden gebruikt bij het aangeven van welke episodes wel en niet gezien zijn. Het plaatje hieronder laat dit iets duidelijker zien.
![](doc/databasefout22januari.png)
- Daarnaast ben ik bezig geweest met het aan de gebruiker laten zien welke episodes hij/zij al heeft gezien door middel van de checkboxes in de expandable listview. Dit is bijna gelukt, echter heeft het soms een bugg dat de verkeerde checkbox wordt aangevinkt. Tijdje bezig geweest met uitzoeken waarom maar ben er nog niet uitgekomen.

### Dinsdag 23 januari 2018
- De expandable listview laat nu zien welke episodes er gezien zijn. De bug is verholpen. 
Echter is er een nieuwe bij gekomen. De eerte keer dat een seizoen laadt wordt namelijk niet getoond welke episodes gezien zijn, maar na scrollen wel.
Soms is het alleen tijdens het scrollen zo dat opeens alle checkboxes gecheckt worden van andere seizoenen. Dit gebeurd wanneer alle seizoenen 'open geklapt' zijn en er dan gescrolld wordt.
- Volg functionaliteit toegevoegd! Het is nu dus mogelijk om mensen te volgen (mensen die gevolgd worden staan nu in de database).
- Hele code doorgekamt en TO-DO's toegevoegd
- Database structuur nog iets aangepast (zie hieronder)


![](doc/DatabaseStructuur.png)

### Woensdag 24 januari 2018
- Nogmaals geprobeerd de expandablelistview te fixen, nog niet helemaal gelukt
- Bij UserDetailsFragment laten zien welke series iemand volgt en bij welke episode de persoon is
- Database structuur nog iets aangepast, series worden nu opgeslagen op is in plaats van op season.
- Wat fragments errors gefixt
- Als er ingelogd is zijn de inlog en registreeer button niet meer zichtbaar, en als er niet is ingelogt is de uitlog button niet te zien
- Volg functionaliteit werkt nu helemaal
- **Problemen:** Feedback geven of de ingelogde user de episodes van de op geklikte user wel of niet heeft gezien

### Donderdag 25 januari 2018
- Expandable listview bug gefixt! Eindelijk!
- Nu mogelijk om met een long click op een episode in de expandable listview aan te geven of je een episode gezien hebt, en om een episode als niet gezien te markeren.
- LoggedInUserInfo afgemaakt
- Mogelijkheid om episodes uit Firebase te verwijderen en om mensen te ontvolgen
- Bij elke user nu aangegeven of je wel of niet kan praten over de laatste episode die je hebt gezien (in de userdetails, rood betekent dat je verder bent dat de geklikte user en groen betekent dat je net zo ver bent of de geklikte user verder is, waar je dus de episode mee kan bespreken).
- Elke listview heeft nu een listener en stuurt je door naar het goede fragment
- Login bug opgelost (soms werd er niet ingelogd)
- Bij episodedetails wordt aangegeven welke vrienden de episode wel en niet hebben gezien
- Er wordt aangegeven als er geen zoenresultaten zijn op een opdracht
- Bezig geweest met zorgen dat bij het 'back pressen' alle informatie weer wordt geladen, zitten nog enkele kleine buggs in.
- Verder nog aardig wat kleine buggs gefixt om de beta zo compleet mogelijk te maken!

### Vrijdag 26 januari 2018
- Bezig geweest met layout verbeteren
- Enkele bugs gefixt
- Alle bugs tot nu toe geinventariseerd, zodat deze gefixt kunnen worden komende week


## Week 4

### Maandag 29 januari 2018
- Uit de feedback van vrijdag bleek dat het niet helemaal duidelijk is wat de kleuren aangeven in het user details fragment. Daarom is er een dialogfragment toegevoegd die dat uitleggen. Ik zie dit niet echt als een toevoeging van functionaliteit, het is alleen een toevoeging die het iets duidelijker maakt als een user vergeet wat de kleuren betekenen. Zie hieronder.  
![](doc/dialogfragment.png)
- Checkbox error in de child van de explandble listview opgelost
- Interactie design verbeterd
- Geprobeerd om de navigatie tussen fragments te fixen, maar dat is niet helemaal gelukt..
- Code opgeschoond in de volgende files (status: 3/19 java files gedaan):
1. LoginFragment
2. ColorInformationDialogFragment
3. RegisterFragment

### Dinsdag 30 januari 2018:
- Code opgeschoond in de volgende files (status: 9.5/19 java files gedaan):
4. ExpandableListAdapter
5. SeriesOverviewAdapter
6. UsersOverviewAdapter
7. SearchResult
8. Episode
9. Serie
10. EpisodeDetailsFragment (aan begonnen, niet helemaal af)
- Design voor de logged in user info verbeterd
- App crashed niet meer wanneer er niet ingelogd is en alle buttons die niet zichtbaar moeten zijn voor een niet ingelogde user zijn weg. Alle informatie in de app is dus zichtbaar voor een niet ingelogde user, maar de deze user kan geen gebruik maken van functionaliteiten.
- Code Review gedaan met Jessy, de volgende punten kwamen eruit:
1. Switch met één case
-Nesting voorkomen waar mogelijk

2. Enters tussen code als je aan een nieuw onderdeel begint

3. Lege functie

4. Lange class omvormen naar JSONObject
5. Comments nog niet compleet
6. Dubbele en lange code
- **Keuze:** Om te zorgen dat fragments niet leeg zijn wanneer er op een on back pressed wordt geklikt is er gebruik gemaakt van shared preferences. Deze worden in bijna elk file in de onCreate en onPause gezet. Dit moet ook in de onCreate gebeuren aangezien anders de vorige opgeklikte episode tevoorschijn komt. Dit komt omdat in de lifecycle van de Fragment eerst de onCreate wordt aangeroepen en daarna de onPause. De code runt dus altijd de onPause ook.

### Woensdag 31 januari 2018:
- Hard bezig geweest met functies verkorten, helper functies schrijven en beter te voldoen aan de eisen van better code hub.
- Code opgeschoond in de volgende files:
10. EpisodeDetailsFragment (afgemaakt)
11. LastEpisodeSeenAdapter
12. UserDetailsFragment
13. FragmentContainer
14. LoggedInUserProfileFragment
15. FireBaseHelper
16. UserInfoClass
17. UsersOverviewFragment
18. SerieOverviewFragment
19. SerieDetailsFragment (missen nog wat kleine dingen)

### Donderdag 1 februari 2018:
- Geprobeerd laatste dingen aan code netjes te maken, bettercodehub hoger te krijgen etc.
- Nog een bug gefixt in de back navigation
- Proposal geschreven
- Github netjes gemaakt
- Screens gemaakt en toegevoegd





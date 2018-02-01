# Final report

## Omschrijving
Deze applicatie maakt het mogelijk om bij te houden wat de laatste aflevering is die gezien is van een serie. Daarnaast geeft het ook de basis informatie weer over de serie en de episodes, zoals het plot en de imdb rating. Door middel van een zoekfunctie kunnen series worden gevonden. 
Bovendien is het ook mogelijk om gebruikers te volgen. Gebruikers kunnen worden gevonden in een overzicht van de gebruikers. De app voorkomt ook dat de gebruiker spoilers geeft zijn vrienden. Dit doet de app door op de pagina van gebruiker informatie met kleuren aan te geven of de ingelogde gebruiker kan praten over de episode met de bekeken gebruikers.
De tot nu toe bekeken series en gevolgde gebruikers worden weergeven in een overzicht van informatie van de ingelogde gebruiker, zodat deze niet steeds in de 'database' hoeven worden terug gevonden.

!SCREENSHOTT

## Technical design

### Class beschrijving (op volgorde van flow door app heen):
- *Listerners*: naast alle onderstaande java files worden er in sommige files ook classen aangemaakt voor de listeners. Deze classen heb ik hier niet apart benoemd aangezien het dan een grote lijst van telkens hetzelfde wordt. De classen die een OnItemClickListener implementeren kijken of er op de listview wordt geklikt en handelen dat. De class die OnChildClickListener implementeerd kijkt naar of er op een child van de expandable listview wordt geklikt. De classen die en ValueEventListener implementeren halen data uit Firebase en houden bij of deze verandert.

- "FragmentContainer.java": Deze class bevat de container waar de rest van de fragments in geplaatst worden. Het zorgt ervoor dat de bottom navigatie werkt en dat het eerste fragment bij het opstarten van de app in de container wordt geplaatst. Daarnaast maakt de class back pressen mogelijk en zorgt het dat de fragments uit de backstack gehaald worden bij een back presse.

-*SeriesOverviewFragment.java*: Deze class maakt het mogelijk om series in de API te zoeken. In de edittext kan een zoekopdracht worden ingevoerd en met de button kan deze worden uitgevoegd. Het stuurt een request naar de API met een zoek opdracht. Vervolgens wordt de JSON reponse geparsed en wordt de informatie per serie opgeslagen in een instance van class SearchResult. Van deze data worden de titel van de serie en de poster gebruikt in de listview. De imdbid wordt gebruikt wanneer er op een serie geklikt wordt, want deze wordt meegegeven aan het volgende fragment. 

-*SeriesOverviewAdapter.java*:	Deze class krijgt data doorgestuurd uit de SeriesOVerviewFragment en maakt hier een listview van.

-*SearchResult.java*: Class die de basis informatie van een serie die als zoekresultaat getoond moet worden bevat. Wordt gebruikt bij het maken van de listview met zoekresultaten (in SeriesOverviewFragment.java).

-*SerieDetailsFragment.java*: Deze class toont informatie van een serie, waaronder alle seizoenen en alle episodes. Het haalt de serie imdbid uit de bundle, die SeriesOVerviewFragment erin heeft gestopt. Vervolgens stuurt het een stringrequest naar de API om informatie over deze serie op te halen. 

-*ExpandableListAdapter.java*: Deze class maakt de expandable listview voor het SerieDetailsFragment. Het zet de juiste data in de parents en de childs. Daarnaast checkt het van elke episode (de childs) of de episode al gezien is door de ingelogde gebruiker. Dit wordt gedaan door te kijken of de episode al in FireBase staat. Als deze al gezien is wordt de checkbox in de row layout 'gechecked'. Als deze nog niet gezien is blijft deze 'unchecked'.
-*Serie.java:*

- *Episode.java*: Class die de basis informatie van een episode/aflvering bevat. Wordt gebruikt voor het maken van de expandable listview (in SerieDetailsFragment.java).

-*EpisodeDetailsFragment.java*: Deze class haalt de episode informatie uit de API aan de hand van de imdbid die meegeven is uit SerieDetailsFragment. Vervolgens plaatst het deze informatie in de textview en imageview. De bijbehorende xml file bevat ook een "I've seen this". Met deze button is het ook mogelijk om een episode toe te voegen aan Firebase en daarmee te markeren als gezien. Hier wordt ook weer gebruik gemaakt van de functies uit de FireBaseHelper class. Het fragment toont daarnaast ook van elke gevolgde gebruiker of zij de episode wel of niet gezien hebben. Hiervoor wordt in de database gekeken naar welke gebruikers de ingelogde gebruiker volgt en vervolgens wordt er van elke gebruiker die gevolgd wordt gekeken of zij de episode gezien hebben.

- *FirebaseHelper.java*: Helper class die functies bevat die een episode toevoegen aan Firebase en eruit verwijderd.

- *UsersOverviewFragment.java*:	Haalt alle users uit Firebase en laat de usernames van de users zien in een listview. Door op een user te klikken wordt er naar de UserDetailsFragment gegaan, met informatie over de geklikte user. 

- *UsersOverviewAdapter.java*:	Deze class maakt de listview met usernames voor de UsersOverviewFragment.
	
- *UserDetailsFragment.java*: Deze class zorgt ervoor dat de informatie van een user zichtbaar is in de UI. Het krijgt een userID mee van het UsersOverviewFragment, waarmee het de data van de user ophaald uit FireBase. De data die opgehaald wordt bestaat onder andere uit het serieID en de laatste episode die ervan gezien is (dus de episode met het hoogste season nummer en hoogste episode nummer). Daarnaast haalt het ook de data op van de ingelogde user. De informatie over welke series een user heeft gekeken en de laatste episode worden weergeven in een listview. Deze listview vergelijkt ook de laatste episode die geziek is door de gebruiker en de laatste die gezien is door de ingelogde gebruiker. Hiermee wordt bepaald of de ingelogde user wel of niet de laatste episode die hij heeft gezien, zou kunnen bespreken met de gebruiker zonder spoilers te geven. Dit wordt aangegeven door de laatste episode die gezien is rood, groen of grijs te maken.

- *LastEpisodeSeenAdapter.java*: Deze class maakt de listview voor het UserDetailsFrag,emt. Het vergelijkt de data van de twee hashmaps die hij krijgt. Wanneer de ingelogde gebruiker verder is in de serie dan de geklikte gebruiker, wordt het episode number rood. Wanneer het andersom is wordt het episode nummer groen. Wanneer de ingelogde gebruiker de serie van de geklikte gebruiker niet kijkt wordt/blijft deze grijs.


- *ColorInformationDialogFragment.java*: Dit is een class die een dialog laat zien, dat kort uitlegd welke kleuren wat betekenen. Het enigste wat deze class doet is de layout inflaten en de close button clickable maken.

- *LoggedInUserProfileFragment.java*: Deze class bepaald of er een gebruiker is ingelogd en past op basis daarvan de UI aan. Wanneer er geen gebruiker is ingelogd wordt er een login knop en een registreer knop weergeven. Wanneer er wel een gebruiker is ingelogd haalt het de informatie van deze gebruiker op aan de hand van zijn ID. Vervolgens de series waarvan minstens een episode gekeken is weergeven in een listview. Daarnaast worden ook de gebruikers die gevolgt zijn weergeven in een andere listview. Deze listviews worden weer clickable gemaakt, waardoor deze eigenlijk als een soort shortcut naar de serie of gebruikers dienen. Op deze manier hoef je niet elke keer door de API te zoeken naar een serie die je al kijkt.

- *LoginFragment.java*:  Deze class haalt ingevoerde informatie op uit de editText van het email address en password. Vervolgens kijkt het of deze informatie overeenkomt met de informatie uit FireBase. Als dit het geval is wordt de gebruikers ingelogd en naar de LoggedInUserInfo Fragment gestuurd. Wanneer dit niet zo is krijgt de gebruiker een melding om het opnieuw te proberen.

- *RegisterFragment.java*: Deze class haalt ingevoerde informatie op uit de editText van het email address, de username en password. Vervolgens zet het deze informatie in FireBase, door het emailadress en het wachtwoord naar de authentication van FireBase te sturen en de username samen met een uniek ID in de user tabel te zetten, door er tijdelijk een instance van class UserInfo te maken en die in FireBase te zetten. Wanneer de registratie succesvol is wordt de gebruiker doorgestuurd naar de LoggedInUserInfo en zo niet krijgt hij een melding. 

- *UserInfo.java*: Class die de basis informatie van de gebruiker bevat. Wordt gebruikt bij het registreren (in Register.java).

## Veranderingen ten opzichte van het design document/Keuzes:
Ten eerste wordt de Jikan API niet gebruikt. Het plan was om deze ook te gebruiken, maar deze API geeft de data in een hele andere format, waardoor het vrij complex werd om dezelfde fragments voor alle twee de APIs te laten werken. Na feedback van de presentatie in week 1 heb ik ervoor gekozen om dit achterwege te laten en alleen door te gaan met de OMDb API.  

Een ander element wat ik heb aangepast is dat er geen rekening wordt gehouden met series die niet op volgorde worden gekeken (hier bedoel ik mee dat er sommige series zijn waar je episode 10 ook voor episode 1 kan kijken, omdat de afleveringen niet heel nauw met elkaar verbonden zijn). Echter heb ik dit na feedback van de presentaties in week 1 ook niet gedaan. Dit zou mijn onnodig complex maken voor maar een heel klein aantal series, aangezien de meeste wel op volgorde worden gekeken.  

Daarnaast is het design aangepast. In het design document staat bij de SerieDetailsActivity nog een normale listview voor welke serie afgebeeld. Dit is uiteindelijk een expandable listview geworden. In SerieDetailsActivity wordt de imdb rating aangegeven in een geel bolletje, dit is uiteindelijk weergeven in een textview. Daarnaast staat er minder informatie afgebeeld dan er uiteindelijk in de app gekomen in. In de app staat namelijk de release dat, het plot, de imdb rating en de awards. Bij de EpisodeDetailsActivity staat ook minder informatie afgebeeld dan er uiteindelijk in de app gekomen is. Het geeft bijvoorbeeld ook de director, writer, language en genre weer. Er is hiervoor gekozen omdat dit mooier uitkwam in het design en het best interessant is om deze informatie over de series en episodes te zien om bijvoorbeeld te bepalen of je ze wilt kijken.  

Aan de hand van de feedback van de presentaties is er bij het UserProfileFragment nog een dialogfragment toegevoegd. Deze staat niet in het design document. Sommige mensen vonden het vrij onduidelijk wat de rode kleur en wat de groene kleur inhielden. Persoonlijk vond ik het best duidelijk: groen betekent dat je de episode kan bespreken met je vriend zonder spoilers te geven en rood betekend dat dit niet kan. Om het echter toch iets duidelijker te maken voor gebruikers die bijvoorbeeld de app voor het eerst gebruiken is er een imagebutten in de vorm van een informatie teken toegevoegd, die een dialogfragment opent.   

Qua classen is er veel veranderd. Ik heb er namelijk voor gekozen om fragments te gebruiken. Dit waren in het design document nog series. Daarnaast is er nog een SearchResult class bij gekomen om data in op te slaan. Ook ben ik in het design document niet ingegaan om de adapters, daar zijn er dus ook nog enkele bijgekomen. Tenslotte is er ook nog een helper class toegevoegd.  

De database structuur is uiteindelijk ook heel anders dan hoe die weergeven staat in het design document. Dit is het resultaat van veel trail en error. De structuur is meerdere keren aangepast. Hieronder is te zien hoe de structuur op dit moment is. Deze structuur maakt het makkelijker om alle informatie uit Firebase te krijgen dan de structuur in het design document.  

!SCREEENNNNN

Andere kleine veranderingen zijn:
- Het idee was eerst om mensen te kunnen toevoegen als vriend. Uiteindelijk is er gekozen om een volg systeem te maken, aangezien ik nog een complexe feature implementeren niet ging redden qua tijd.
- Bij het UserProfileActivity is het design aangepast door geen kader om de textview heen te weergeven voor de kleur, maar om de tekst de kleur te geven. Dit maakte het design een stuk rustiger
- In de listview bij de UserProfileActivity is het vrij nutteloos dat er in de listview nog wordt aangegeven dat je een serie volgt, aangezien de kleuren zijn afgestemd op welke episodes je gezien hebt. Volg je een serie niet, is het episode nummer in het grijs weergeven. Dit had ik mij niet gerealiseerd met het maken van het design document.
- Bij het design document was het het idee om ingelogd te moeten zijn om de app te kunnen gebruiken. Uiteindelijk heb ik ervoor gekozen om dit niet te doen. Gebruikers kunnen dus door de series en users heen browsen zonder ingelogd te zijn. Ze kunnen dan alleen geen mensen volgen of episodes als gezien markeren. Er is hiervoor gekozen zodat een user kan checken of al zijn favoriete series en zijn vrienden de app gebruiken, voor hij/zij een account aanmaakt.


## Challenges:

### Expandable listview maken
Een van de grootste challenges van deze applicatie was om de expandable listview te maken. Aangezien ik dit nog nooit gedaan had was dit vrij lastig. Het maken van de listview adapter was vrij simpel. Met het kijken van wat tutorials was dit goed te doen. Het lastige gedeelte kwam doordat ik graag wilde dat in de expandable listview de checkboxes van episodes die al gezien zijn 'gecheckt' zijn. Ik heb daarom de volgende dingen geprobeerd:
- Ik wilde de data die beschrijft of een episode gezien is meegeven aan de adapter. Dit werkte, alleen als de listview voor het eerst laadde werkte het niet, pas na het scrollen.  Dit kwam deels doordat de data nog niet beschikbaar was wanneer de listview werd gemaakt. Na dit te fixen liep ik tegen het volgende probleem aan: soms maakt de adapter een checkbox 'checked' die dat niet hoorde te zijn.
- Ik heb na het bovenstaande te proberen de data opgehaald in de adapter zelf, wanneer een child wordt gemaakt. Echter wilde die ook niet helemaal werken, aangezien FireBase minder snel is dan dat een child gemaakt wordt. Dit kon gefixt worden door een timer, maar dit zorgde voor een onnodig lange delay.
- Uiteindelijk heb ik het weten te fixen: Wanneer een child gemaakt wordt, wordt een functie aangeroepen die bekijkt welke episodes al gezien zijn. Deze maakt in de functie die de informatie uit FireBase haalt, gelijk de checkbox checked. Het is niet de meest mooie manier om het op te lossen in code, maar het werkt. De data ophalen uit FireBase gaat namelijk op de achtergrond door wanneer de andere childs in de listview worden gemaakt. Er zit wel een kleine vertraging in de checkbox: het duurt soms enkele milliseconde voor de episodes die gezien zijn gevonden zijn.
Het vervelende aan dit probleem was dat ik ongeveer 1.5 week kwijt ben geweest aan het aanleveren van de data aan de expandable listview. Hierdoor kwam de volgende challenge: heel veel doen in heel weinig tijd. Omdat ik de week van de beta versie de expandable listview pas afhad, moest ik nog veel doen in een paar dagen.  

### Episodes in Firebase zetten
Om een episode als gezien te markeren moet deze in FireBase gezet worden. Echter was het nog best lastig om de structuur van FireBase zo te krijgen dat het makkelijk erin te zetten is en eruit te krijgen. Ik heb de structuur meerdere keren moeten aanpassen. Daarnaast was het ook een challenge om te zorgen dat de functies die iets uit FireBase halen of erin zetten, niet te lang te maken. Door een nested structuur in FireBase zijn er veel if en else statements nodig om de juiste data bruikbaar de maken in de code. 

### Fragments en onBackPressed
Ik heb besloten om fragments te gaan gebruiken zodat ik op een makkelijke manier een bottom navigatie heb kunnen maken. Echter was mijn ervaring met fragments vrij minimaal, waardoor ik tegen vrij veel bugs ben aangelopen. Ik kreeg het bijvoorbeeld vrij lang niet voor elkaar om als er op de terug knop wordt gedrukt, de data te weergeven in het fragment. Deze bleef namelijk leeg. Uiteindelijk heb ik het kunnen fixen door SharedPreferences te gebruiken, en daarmee de id van de serie of user op te slaan, om in de onResume dan weer de data op te halen. Maar daarmee loop je gelijk tegen het probleem aan dat de onResume en onCreate beide altijd worden aangeroepen wanneer een fragment voor het eerst opent. Hierdoor wordt de data 2x opgehaald, maar in de onResume kan daardoor een andere userid staan dan in de oncreate. Dit is uiteindelijk gefixt (na veel proberen samen met een assistent) door in de onCreate de sharedprefs al een keer te overschrijven met het nieuwe id, zodat in de onResume geen oude informatie meer staat. Dit is ook weer een probleem waar ik vrij lang over heb gedaan om het te kunnen fixen.

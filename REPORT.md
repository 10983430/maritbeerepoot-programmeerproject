# Final report

## Omschrijving
Deze applicatie maakt het mogelijk om bij te houden wat de laatste aflevering is die gezien is van een serie. Daarnaast geeft het ook de basis informatie weer over de serie en de episodes, zoals het plot en de imdb rating. Door middel van een zoekfunctie kunnen series worden gevonden. 
Bovendien is het ook mogelijk om gebruikers te volgen. Gebruikers kunnen worden gevonden in een overzicht van de gebruikers. De app voorkomt ook dat de gebruiker spoilers geeft zijn vrienden. Dit doet de app door op de pagina van gebruiker informatie met kleuren aan te geven of de ingelogde gebruiker kan praten over de episode met de bekeken gebruikers.
De tot nu toe bekeken series en gevolgde gebruikers worden weergeven in een overzicht van informatie van de ingelogde gebruiker, zodat deze niet steeds in de 'database' hoeven worden terug gevonden.

!SCREENSHOTT

## Technical design

### Class beschrijving:
- *Listerners*: naast alle onderstaande java files worden er in sommige files ook classen aangemaakt voor de listeners. Deze classen heb ik hier niet apart benoemd aangezien het dan een grote lijst van telkens hetzelfde wordt. De classen die een OnItemClickListener implementeren kijken of er op de listview wordt geklikt en handelen dat. De class die OnChildClickListener implementeerd kijkt naar of er op een child van de expandable listview wordt geklikt. De classen die en ValueEventListener implementeren halen data uit Firebase en houden bij of deze verandert.
-*SeriesOverviewFragment.java*: Deze class maakt het mogelijk om series in de API te zoeken. In de edittext kan een zoekopdracht worden ingevoerd en met de button kan deze worden uitgevoegd. Het stuurt een request naar de API met een zoek opdracht. Vervolgens wordt de JSON reponse geparsed en wordt de informatie per serie opgeslagen in een instance van class SearchResult. Van deze data worden de titel van de serie en de poster gebruikt in de listview. De imdbid wordt gebruikt wanneer er op een serie geklikt wordt, want deze wordt meegegeven aan het volgende fragment. 
-*SeriesOverviewAdapter.java*:	Deze class krijgt data doorgestuurd uit de SeriesOVerviewFragment en maakt hier een listview van.
-*SearchResult.java*:  ??

-*SerieDetailsFragment.java*: Deze class toont informatie van een serie, waaronder alle seizoenen en alle episodes. Het haalt de serie imdbid uit de bundle, die SeriesOVerviewFragment erin heeft gestopt. Vervolgens stuurt het een stringrequest naar de API om informatie over deze serie op te halen. 

-*ExpandableListAdapter.java*:
-*Serie.java:*

- *Episode.java*: ??
-*Episode.java*:
-*EpisodeDetailsFragment.java*:
-*FirebaseHelper.java*:

FragmentContainer.java:
LastEpisodeSeenAdapter.java:
LoggedInUserProfileFragment.java:
LoginFragment.java:
RegisterFragment.java:



	
UserDetailsFragment.java:
UserInfo.java:
UsersOverviewAdapter.java:	
UsersOverviewFragment.java:	
ColorInformationDialogFragment.java



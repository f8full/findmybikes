# # findmybikes -
[![Build Status](https://travis-ci.org/f8full/findmybikes.svg?branch=master)](https://travis-ci.org/f8full/findmybikes)

<a href='https://play.google.com/store/apps/details?id=com.ludoscity.findmybikes&utm_source=global_co&utm_medium=prtnr&utm_content=Mar2515&utm_campaign=PartBadge&pcampaignid=MKT-Other-global-all-co-prtnr-py-PartBadge-Mar2515-1'><img alt='Get it on Google Play' src='https://play.google.com/intl/en_us/badges/images/generic/en_badge_web_generic.png'/></a>

****
##### note
As stated in the [privacy policy], no personal data is neither collected nor used by #findmybikes.
However, some non personal and non identifiable data is collected and published publicly on Twitter.
You can see the data output on the dedicated bot timeline
#### https://twitter.com/findmybikesdata
This published data is the essence of #ludOScity : smarter cities through smart interactions
with their inhabitants. This data could be used to visualize and/or evaluate bikeshare services.
Anyone, including you, could come up with some idea to use it.


#### Twitter output data format specification

Data is tweeted **when the station closest to you doesn't provide service**. When that happen, #ludOS publishes
a status update through [@findmybikesdata]. This tweet mentions what was the closest station **that could provide you service**.

    #findmybixibikes bike is not closest! Bikes:X BAD at IDIDIDIDIDIDIDIDIDIDIDIDIDIDIDID ~XXmin walk stationnamestationnamestation deduplicateZ
sample *#findmybixibikes bike is not closest! Bikes:7 BAD at f132843c3c740cce6760167985bc4d17 ~2min walk Brébeuf / Laurier deduplicate0*

If some stations were discarded in the selection process (because they'd be closer but could not provide service either)
Tweet replies are generated, providing more details on discarded stations

    #findmybixibikes discarded closer! Bikes:Y CRI at IDIDIDIDIDIDIDIDIDIDIDIDIDIDIDID stationnamestationnamestationnamestationnamestationnam
sample *#findmyvlovbikes discarded closer! Bikes:0 CRI at 962c34a494d29737aa7c534f85566949 10102 - DOUA / G. BERGER*

All status updates (root or replies) are geotagged with the location of the described station.

Station names are provided for readability. Station id is provided by citibik.es API
To retrieve corresponding station data, see the great API and fantastic project : https://citybik.es/#about

-- part of [ludos.city]

[privacy policy]:https://github.com/f8full/findmybikes/blob/276f9927c2611808704af42d069503bfad99dd3d/Privacy_policy.md
[@findmybikesdata]:https://twitter.com/findmybikesdata
[GET IT ON GOOGLE PLAY]:https://play.google.com/store/apps/details?id=com.ludoscity.findmybikes
[ludos.city]:http://ludos.city


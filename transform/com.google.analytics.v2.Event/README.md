# Test locally
npm start

# View Item List
curl --header "Content-Type: application/json"   --request POST   --data '{
     "message": {
       "attributes":{"namespace":"com.google.analytics.v2","name":"Hit","topic":"com.google.analytics.v2.Hit-collector"},
       "data":"eyJwYXlsb2FkIiA6ICJlbj1wYWdlX3ZpZXdcbmVuPXZpZXdfaXRlbV9saXN0Jl9ldD0xMCZwcjE9bm1UcmlibGVuZCUyMEFuZHJvaWQlMjBULVNoaXJ0fmlkMTIzNDV+cHIxNS4yNX5ickdvb2dsZX5jYUFwcGFyZWx+azBpdGVtX2NhdGVnb3J5XzJ+djBNZW5zfmsxaXRlbV9jYXRlZ29yeV8zfnYxU2hpcnRzfmsyaXRlbV9jYXRlZ29yeV80fnYyVHNoaXJ0c352YUdyYXl+bG5TZWFyY2glMjBSZXN1bHRzfmxpU1IxMjN+bHAxfnF0MSZwcjI9bm1Eb251dCUyMEZyaWRheSUyMFNjZW50ZWQlMjBULVNoaXJ0fmlkNjc4OTB+cHIzMy43NX5ickdvb2dsZX5jYUFwcGFyZWx+azBpdGVtX2NhdGVnb3J5XzJ+djBNZW5zfmsxaXRlbV9jYXRlZ29yeV8zfnYxU2hpcnRzfmsyaXRlbV9jYXRlZ29yeV80fnYyVHNoaXJ0c352YUJsYWNrfmxuU2VhcmNoJTIwUmVzdWx0c35saVNSMTIzfmxwMn5xdDEiLCJoZWFkZXJzIiA6IHsiZm9vIjoiYmFyIn0sInF1ZXJ5U3RyaW5nIiA6ICJ2PTImdGlkPUctSzhNUUVXU0QzOCZndG09Mm9lNXIwJl9wPTY2MDk2Mjc0OCZzcj0xNTM2eDg2NCZ1bD1zdi1zZSZjaWQ9NTA0MTcyMjgxLjE1ODI3MzM3NjgmZGw9aHR0cHMlM0ElMkYlMkZyb2JlcnRzYWhsaW4uY29tJTJGJmRyPSZkdD1yb2JlcnRzYWhsaW4uY29tJnNpZD0xNTkxNjc2MDYyJnNjdD0xNiZzZWc9MSZfcz0xIn0=",
       "messageId": "136969346945"
     },
     "subscription": "projects/myproject/subscriptions/mysubscription"
   }'   "http://localhost:8080/topic/tmp"


{
     "message": {
       "attributes":{"namespace":"com.google.analytics.v2","name":"Hit","topic":"com.google.analytics.v2.Hit-collector"},
       "data": "data":"ewogICJwYXlsb2FkIiA6ICJlbj1wYWdlX3ZpZXcKZW49dmlld19pdGVtX2xpc3QmX2V0PTEwJnByMT1ubVRyaWJsZW5kJTIwQW5kcm9pZCUyMFQtU2hpcnR+aWQxMjM0NX5wcjE1LjI1fmJyR29vZ2xlfmNhQXBwYXJlbH5rMGl0ZW1fY2F0ZWdvcnlfMn52ME1lbnN+azFpdGVtX2NhdGVnb3J5XzN+djFTaGlydHN+azJpdGVtX2NhdGVnb3J5XzR+djJUc2hpcnRzfnZhR3JheX5sblNlYXJjaCUyMFJlc3VsdHN+bGlTUjEyM35scDF+cXQxJnByMj1ubURvbnV0JTIwRnJpZGF5JTIwU2NlbnRlZCUyMFQtU2hpcnR+aWQ2Nzg5MH5wcjMzLjc1fmJyR29vZ2xlfmNhQXBwYXJlbH5rMGl0ZW1fY2F0ZWdvcnlfMn52ME1lbnN+azFpdGVtX2NhdGVnb3J5XzN+djFTaGlydHN+azJpdGVtX2NhdGVnb3J5XzR+djJUc2hpcnRzfnZhQmxhY2t+bG5TZWFyY2glMjBSZXN1bHRzfmxpU1IxMjN+bHAyfnF0MSIsCiAgImhlYWRlcnMiIDogeyJmb28iOiJiYXIifSwKICAicXVlcnlTdHJpbmciIDogInY9MiZ0aWQ9Ry1LOE1RRVdTRDM4Jmd0bT0yb2U1cjAmX3A9NjYwOTYyNzQ4JnNyPTE1MzZ4ODY0JnVsPXN2LXNlJmNpZD01MDQxNzIyODEuMTU4MjczMzc2OCZkbD1odHRwcyUzQSUyRiUyRnJvYmVydHNhaGxpbi5jb20lMkYmZHI9JmR0PXJvYmVydHNhaGxpbi5jb20mc2lkPTE1OTE2NzYwNjImc2N0PTE2JnNlZz0xJl9zPTEiICAKfQ==",
       "messageId": "136969346945"
     },
     "subscription": "projects/myproject/subscriptions/mysubscription"
   }


"data":{"payload" : "en=page_view\\nen=view_item_list&_et=10&pr1=nmTriblend%20Android%20T-Shirt~id12345~pr15.25~brGoogle~caApparel~k0item_category_2~v0Mens~k1item_category_3~v1Shirts~k2item_category_4~v2Tshirts~vaGray~lnSearch%20Results~liSR123~lp1~qt1&pr2=nmDonut%20Friday%20Scented%20T-Shirt~id67890~pr33.75~brGoogle~caApparel~k0item_category_2~v0Mens~k1item_category_3~v1Shirts~k2item_category_4~v2Tshirts~vaBlack~lnSearch%20Results~liSR123~lp2~qt1","headers" : {"foo":"bar"},"queryString" : "v=2&tid=G-K8MQEWSD38&gtm=2oe5r0&_p=660962748&sr=1536x864&ul=sv-se&cid=504172281.1582733768&dl=https%3A%2F%2Frobertsahlin.com%2F&dr=&dt=robertsahlin.com&sid=1591676062&sct=16&seg=1&_s=1"}


curl --header "Content-Type: application/json"   --request POST   --data '{"data":{"v":"2","tid":"G-K8MQEWSD38","gtm":"2oe4f0","_p":"1992368660","sr":"1920x1080","ul":"sv-se","cid":"504172281.1582733768","dl":"https%3A%2F%2Frobertsahlin.com%2F","dr":"https%3A%2F%2Fwww.google.se%2F","dt":"robertsahlin.com","sid":"1587737451","sct":"2","seg":"1","_s":"1","en":"view_item_list","_et":"6","pr1":"nmTriblend%20Android%20T-Shirt~id12345~pr15.25~brGoogle~caApparel~k0item_category_2~v0Mens~k1item_category_3~v1Shirts~k2item_category_4~v2Tshirts~vaGray~lnSearch%20Results~liSR123~lp1~qt1","pr2":"nmDonut%20Friday%20Scented%20T-Shirt~id67890~pr33.75~brGoogle~caApparel~k0item_category_2~v0Mens~k1item_category_3~v1Shirts~k2item_category_4~v2Tshirts~vaBlack~lnSearch%20Results~liSR123~lp2~qt1"},"headers":{"user-agent":"curl/7.52.1","host":"localhost:8080"}, "attributes":{"namespace":"com.google.analytics.v2","name":"Hit","topic":"com.google.analytics.v2.Hit-collector"}}'   "http://localhost:8080/"

curl --header "Content-Type: application/json"   --request POST   --data '{"data":{"v":"2","tid":"G-K8MQEWSD38","gtm":"2oe4f0","_p":"1992368660","sr":"1920x1080","ul":"sv-se","cid":"504172281.1582733768","dl":"https%3A%2F%2Frobertsahlin.com%2F","dr":"https%3A%2F%2Fwww.google.se%2F","dt":"robertsahlin.com","sid":"1587737451","sct":"2","seg":"1","_s":"1","en":"view_item_list","_et":"6","pr1":"nmTriblend%20Android%20T-Shirt~id12345~pr15.25~brGoogle~caApparel~k0item_category_2~v0Mens~k1item_category_3~v1Shirts~k2item_category_4~v2Tshirts~vaGray~lnSearch%20Results~liSR123~lp1~qt1","pr2":"nmDonut%20Friday%20Scented%20T-Shirt~id67890~pr33.75~brGoogle~caApparel~k0item_category_2~v0Mens~k1item_category_3~v1Shirts~k2item_category_4~v2Tshirts~vaBlack~lnSearch%20Results~liSR123~lp2~qt1"},"headers":{"user-agent":"curl/7.52.1","host":"localhost:8080"}, "attributes":{"namespace":"com.google.analytics.v2","name":"Hit","topic":"com.google.analytics.v2.Hit-collector"}}'   "https://europe-west1-datahem.cloudfunctions.net/com_google_analytics_v2_Event"



  # Deploy Cloud Functions from shell
  gcloud functions deploy com_google_analytics_v2_Event --region europe-west1 --runtime nodejs10 --trigger-http --max-instances 5

gcloud functions add-iam-policy-binding com_google_analytics_v2_Event \
  --region europe-west1 \
  --member=allAuthenticatedUsers \
  --role="roles/cloudfunctions.invoker"
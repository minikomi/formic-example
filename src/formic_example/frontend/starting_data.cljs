(ns formic-example.frontend.starting-data)

(def starting-data
  {:page-data
   {:title-text "title text value",
    :hero-image
    "https://images.unsplash.com/photo-1546946663-ac4155bd8ec6?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjQ5OTc1fQ",
    :date-created "2014-06-12",
    :title-type "wide",
    :title-color "#8ed1fc",
    :subtitle-text "rwararaw",
    :compound :page},
   :article-body
   [{:title "elephants are cool",
     :body
     [{:insert
       "Elephants are herbivorous and can be found in different habitats including "}
      {:attributes {:link "https://en.wikipedia.org/wiki/Savanna"},
       :insert "savannahs"}
      {:insert ", forests, deserts, and "}
      {:attributes {:link "https://en.wikipedia.org/wiki/Marsh"},
       :insert "marshes"}
      {:insert
       ". They prefer to stay near water. They are considered to be a "}
      {:attributes
       {:link "https://en.wikipedia.org/wiki/Keystone_species"},
       :insert "keystone species"}
      {:insert
       " due to their impact on their environments. Other animals tend to keep their distance from elephants while predators, such as "}
      {:attributes {:link "https://en.wikipedia.org/wiki/Lion"},
       :insert "lions"}
      {:insert ", "}
      {:attributes {:link "https://en.wikipedia.org/wiki/Tiger"},
       :insert "tigers"}
      {:insert ", "}
      {:attributes {:link "https://en.wikipedia.org/wiki/Hyena"},
       :insert "hyenas"}
      {:insert ", and any "}
      {:attributes {:link "https://en.wikipedia.org/wiki/Canidae"},
       :insert "wild dogs"}
      {:insert
       ", usually target only young elephants (or \"calves\"). Elephants have a "}
      {:attributes
       {:link
        "https://en.wikipedia.org/wiki/Fission%E2%80%93fusion_society"},
       :insert "fission–fusion society"}
      {:insert
       " in which multiple family groups come together to socialise. Females (\"cows\") tend to live in family groups, which can consist of one female with her calves or several related females with offspring. The groups are led by an individual known as the "}
      {:attributes {:link "https://en.wikipedia.org/wiki/Matriarchy"},
       :insert "matriarch"}
      {:insert ", often the oldest cow.\n\na"}
      {:attributes {:list "bullet"}, :insert "\n"}
      {:insert "b"}
      {:attributes {:indent 1, :list "bullet"}, :insert "\n"}
      {:insert "c"}
      {:attributes {:indent 2, :list "bullet"}, :insert "\n"}
      {:insert "d"}
      {:attributes {:indent 1, :list "bullet"}, :insert "\n"}
      {:insert "e"}
      {:attributes {:list "bullet"}, :insert "\n"}
      {:insert "f"}
      {:attributes {:indent 1, :list "bullet"}, :insert "\n"}
      {:insert "g"}
      {:attributes {:indent 2, :list "bullet"}, :insert "\n"}
      {:insert "h"}
      {:attributes {:indent 3, :list "bullet"}, :insert "\n"}],
     :compound :paragraph}
    {:title "standing",
     :body
     [{:attributes {:bold true}, :insert "Standing"}
      {:insert ", also referred to as "}
      {:attributes {:bold true}, :insert "orthostasis"}
      {:insert ", is a "}
      {:attributes
       {:link "https://en.wikipedia.org/wiki/Human_position"},
       :insert "human position"}
      {:insert " in which the body is held in an upright (\""}
      {:attributes {:bold true}, :insert "orthostatic"}
      {:insert
       "\") position and supported only by the feet.\n\nAlthough seemingly static, the body rocks slightly back and forth from the "}
      {:attributes {:link "https://en.wikipedia.org/wiki/Ankle"},
       :insert "ankle"}
      {:insert " in the "}
      {:attributes
       {:link
        "https://en.wikipedia.org/wiki/Anatomical_terms_of_location#Planes"},
       :insert "sagittal"}
      {:insert
       " plane. The sagittal plane bisects the body into right and left sides. The sway of quiet standing is often likened to the motion of an "}
      {:attributes
       {:link "https://en.wikipedia.org/wiki/Inverted_pendulum"},
       :insert "inverted pendulum"}
      {:insert "."}
      {:attributes
       {:link "https://en.wikipedia.org/wiki/Standing#cite_note-1"},
       :insert "[1]"}
      {:insert "\n"}
      {:attributes
       {:link "https://en.wikipedia.org/wiki/Standing_at_attention"},
       :insert "Standing at attention"}
      {:insert " is a military standing posture, as is "}
      {:attributes {:link "https://en.wikipedia.org/wiki/Stand_at_ease"},
       :insert "stand at ease"}
      {:insert
       ", but these terms are also used in military-style organisations and in some professions which involve standing, such as "}
      {:attributes
       {:link "https://en.wikipedia.org/wiki/Model_(person)"},
       :insert "modeling"}
      {:insert ". "}
      {:attributes {:italic true}, :insert "At ease"}
      {:insert
       " refers to the classic military position of standing with legs slightly apart, not in as formal or regimented a pose as standing at attention. In modeling, "}
      {:attributes {:italic true}, :insert "model at ease"}
      {:insert
       " refers to the model standing with one leg straight, with the majority of the weight on it, and the other leg tucked over and slightly around.\n"}],
     :compound :paragraph}
    {:images
     [{:image
       "https://images.unsplash.com/photo-1546934164-73ef3631f62d?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjQ5OTc1fQ",
       :caption "oh a nice elephant",
       :compound :captioned-image}
      {:image
       "https://images.unsplash.com/photo-1546919992-3b0ccfae06aa?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjQ5OTc1fQ",
       :caption "cool, she's just standing there",
       :compound :captioned-image}],
     :compound :gallery}
    {:title nil,
     :body
     [{:attributes {:bold true}, :insert "Hello, World!"}
      {:insert "\n"}
      {:attributes {:bold true}, :insert "This is a second line."}
      {:insert "\nThis is a demo of the Quilljs Renderer\n\n\n"}
      {:attributes {:italic true, :bold true}, :insert " bold italic "}
      {:attributes {:italic true}, :insert "only italic"}
      {:insert "\n\n"}
      {:attributes {:italic true}, :insert "new line"}
      {:insert "\n\nnew line\n\nlist"}
      {:attributes {:list "bullet"}, :insert "\n"}
      {:insert "another list"}
      {:attributes {:list "bullet"}, :insert "\n"}
      {:insert "indented list  a a"}
      {:attributes {:indent 1, :list "bullet"}, :insert "\n"}
      {:insert "further indented list"}
      {:attributes {:indent 2, :list "bullet"}, :insert "\n"}
      {:insert "back to level 2 baby"}
      {:attributes {:indent 1, :list "bullet"}, :insert "\n"}
      {:insert "oooh yeah"}
      {:attributes {:indent 1, :list "bullet"}, :insert "\n"}
      {:insert "top level yo"}
      {:attributes {:list "bullet"}, :insert "\n"}
      {:insert "top level list"}
      {:attributes {:list "bullet"}, :insert "\n"}
      {:insert "\nthis is some text "}
      {:attributes {:link "http://google.com"}, :insert "with a link"}
      {:insert "\n\nend\n\n\noooh\n"}],
     :compound :paragraph}]})

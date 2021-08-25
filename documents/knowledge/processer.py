import yaml

ELEMENTS_YAML = "elements.yaml"

with open(ELEMENTS_YAML, "r", encoding="utf-8") as elements:
    try:
        print(yaml.safe_load(elements))
    except yaml.YAMLError as ex:
        print(ex)

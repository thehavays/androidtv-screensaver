import random
import string
import urllib.request
import xml.etree.cElementTree as ElementTree
from pathlib import Path

import requests
from bs4 import BeautifulSoup


def check_url(url):
    try:
        response = urllib.request.urlretrieve(url)
    except Exception:
        return None
    return response


def get_html(url):
    page = requests.get(url)
    return BeautifulSoup(page.content, "html.parser")


def get_image_links_dconnoly(soup):
    result_set = soup.find_all("a", attrs={"target": "_blank", "rel": "noopener noreferrer"})
    image_names = []
    for result in result_set:
        image_names.append(result['href'])
    return image_names


def save_images_folder(folder_name, image_links):
    image_names = []
    for idx, image_link in enumerate(image_links):
        name = (''.join(random.SystemRandom().choice(string.ascii_lowercase) for _ in range(10))) + ".jpg"
        image_path = folder_name + "/" + name
        if check_url(image_link):
            urllib.request.urlretrieve(image_link, image_path)
            image_names.append(name)
            print(image_path)
    return image_names


def create_xml_file(folder_name, image_names):
    resources = ElementTree.Element("resources")
    array = ElementTree.SubElement(resources, "array", name="images")

    for image_name in image_names:
        ElementTree.SubElement(array, "item").text = image_name

    filled_tree = ElementTree.ElementTree(resources)
    filled_tree.write(folder_name + "/images_array.xml")
    line_inserter(folder_name)


def line_inserter(folder_name):
    with open(folder_name + "/images_array.xml", 'a+') as file:
        content = file.read()
        file.seek(0, 0)
        file.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>".rstrip('\r\n') + '\n' + content)


def get_images_from_url(url):
    soup = get_html(url)
    image_links = None
    folder_name = None
    if url == "https://github.com/dconnolly/Chromecast-Backgrounds":
        folder_name = "dconnolly"
        Path(folder_name).mkdir(parents=True, exist_ok=True)
        image_links = get_image_links_dconnoly(soup)

    if folder_name is not None and image_links is not None:
        image_paths = save_images_folder(folder_name, image_links)
        create_xml_file(folder_name, image_paths)


get_images_from_url("https://github.com/dconnolly/Chromecast-Backgrounds")

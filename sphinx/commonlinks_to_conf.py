#!/usr/bin/env python

import re
from pathlib import Path


def get_files(path_str: str):
    result = []
    path = Path(path_str)
    for entry in path.iterdir():
        if entry.is_dir():
            for sp in get_files(entry.absolute().__str__()):
                result.append(sp)
        else:
            result.append(entry.absolute().__str__())
    return result


def filter_files(file_paths: list):
    filtered = []
    for file_path in file_paths:
        if file_path.__contains__('class-use') or \
                file_path.__contains__('package-summary') or \
                file_path.__contains__('package-tree') or \
                file_path.__contains__('package-use'):
            continue
        filtered.append(file_path)
    return filtered


def absolute_path_to_relative(file_paths: list):
    relative_paths = []
    for file_path in file_paths:
        relative_paths.append(re.sub(r'.*(apidocs.*)', r'\g<1>', file_path))
    return relative_paths


def relative_paths_to_common_links(relative_paths: list):
    common_links = {}
    for relative_path in relative_paths:
        temp = re.sub(r'.*\/(.*).html', r'\g<1>', relative_path)
        class_name = re.sub(r'.*\.(.*)', r'\g<1>', temp)
        common_links[class_name] = relative_path
    return common_links


files = get_files('apidocs/veslo')
filtered = filter_files(files)
relative = absolute_path_to_relative(filtered)
common_links = relative_paths_to_common_links(relative)

common_links_def_body = 'commonlinks = {\n'
for k, v in common_links.items():
    common_links_def_body += ("    '" + k + "': '" + v + "',\n")
common_links_def_body += '}\n'

with open("conf.py", "a") as conf_py:
    conf_py.write(common_links_def_body)
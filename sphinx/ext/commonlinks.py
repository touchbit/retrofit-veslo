#  Copyright 2021-2022 Shaburov Oleg
#
#  Licensed under the Apache License, Version 2.0 (the "License");
#  you may not use this file except in compliance with the License.
#  You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
#  Unless required by applicable law or agreed to in writing, software
#  distributed under the License is distributed on an "AS IS" BASIS,
#  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#  See the License for the specific language governing permissions and
#  limitations under the License.

# -*- coding: utf-8 -*-

import sphinx
from docutils import nodes, utils
from six import iteritems


def make_link_role(base_url):
    def role(typ, rawtext, text, lineno, inliner, options={}, content=[]):
        text = utils.unescape(text)
        if text == '_':
            title = str(typ)
        elif text == '_url_':
            title = base_url
        else:
            title = text
        pnode = nodes.reference(title, title, internal=False, refuri=base_url)
        return [pnode], []

    return role


def setup_link_roles(app):
    for name, base_url in iteritems(app.config.commonlinks):
        app.add_role(name, make_link_role(base_url))


def setup(app):
    app.add_config_value('commonlinks', {}, 'env')
    app.connect('builder-inited', setup_link_roles)
    return {'version': sphinx.__display_version__, 'parallel_read_safe': True}

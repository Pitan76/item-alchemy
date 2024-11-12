"""
  MCPitanLib v2から MCPitanLib v3 への移行補助スクリプト (v1.0)
"""

import os
import re

FOLDER_PATH = '../src/main/java'

# Replacements r'(pattern)': r'(replacement)'
REPLACEMENTS = {
    r'(public|private|protected|abstract)?\s*class\s+(\w+)\s+extends\s+ExtendBlock\s+(implements\s+\w+(\s*,\s*\w+)*)?\s*{': r'\1 class \2 extends CompatBlock \3 {',
    r'(public|private|protected|abstract)?\s*class\s+(\w+)\s+extends\s+ExtendItem\s+(implements\s+\w+(\s*,\s*\w+)*)?\s*{': r'\1 class \2 extends CompatItem \3 {',
    r'public ActionResult onRightClick\(ItemUseEvent (\w+)\) {': r'public StackActionResult onRightClick(ItemUseEvent \1) {',
    r'public ActionResult onRightClick\(BlockUseEvent (\w+)\) {': r'public CompatActionResult onRightClick(BlockUseEvent \1) {',
    r'public ActionResult onRightClickOnBlock\(ItemUseOnBlockEvent (\w+)\) {': r'public CompatActionResult onRightClickOnBlock(ItemUseOnBlockEvent \1) {',
    r'public ActionResult onRightClickOnEntity\(ItemUseOnEntityEvent (\w+)\) {': r'public CompatActionResult onRightClickOnEntity(ItemUseOnEntityEvent \1) {',
    r'import net\.pitan76\.mcpitanlib\.api\.block\.ExtendBlock;': r'import net.pitan76.mcpitanlib.api.block.v2.CompatBlock;',
    r'import net\.pitan76\.mcpitanlib\.api\.item\.ExtendItem;': r'import net.pitan76.mcpitanlib.api.item.v2.CompatItem;',
    r'import net\.pitan76\.mcpitanlib\.api\.block\.CompatibleBlockSettings;': r'import net.pitan76.mcpitanlib.api.block.v2.CompatibleBlockSettings;',
    r'import net\.pitan76\.mcpitanlib\.api\.item\.CompatibleItemSettings;': r'import net.pitan76.mcpitanlib.api.item.v2.CompatibleItemSettings;',
    r'import net\.pitan76\.mcpitanlib\.api\.util\.ItemUtil;': r'import net.pitan76.mcpitanlib.api.util.item.ItemUtil;',
    r'import net\.pitan76\.mcpitanlib\.api\.util\.BlockUtil;': r'import net.pitan76.mcpitanlib.api.util.block.BlockUtil;',
    r'CompatMapCodec\.createCodecOfExtendBlock\(': r'CompatBlockMapCodecUtil.createCodec(',
    r'ItemUtil\.ofBlock\(': 'ItemUtil.create(',
    r'mapColor\(MapColor\.(\w+)\)': r'mapColor(CompatMapColor.\1)',
}

# Imports
ADD_IMPORTS = [
    {
        'insert': 'import net.pitan76.mcpitanlib.api.util.CompatActionResult;',
        'condition': 'public CompatActionResult '
    },
    {
        'insert': 'import net.pitan76.mcpitanlib.api.util.StackActionResult;',
        'condition': 'public StackActionResult '
    },
    {
        'insert': 'import net.pitan76.mcpitanlib.api.block.v2.CompatBlock;',
        'condition': 'extends CompatBlock '
    },
    {
        'insert': 'import net.pitan76.mcpitanlib.api.item.v2.CompatItem;',
        'condition': 'extends CompatItem '
    },
    {
        'insert': 'import net.pitan76.mcpitanlib.core.serialization.codecs.CompatBlockMapCodecUtil;',
        'condition': 'CompatBlockMapCodecUtil.createCodec'
    },
    {
        'insert': 'import net.pitan76.mcpitanlib.api.util.color.CompatMapColor;',
        'condition': 'mapColor(CompatMapColor.'
    }
]


for root, dirs, files in os.walk(FOLDER_PATH):
    for filename in files:
        if filename.endswith('.java'):
            filepath = os.path.join(root, filename)

            with open(filepath, 'r', encoding='utf-8') as file:
                content = file.read()

            content2 = content

            # replace pattern
            for pattern, replacement in REPLACEMENTS.items():
                content = re.sub(pattern, replacement, content)

            # add import by condition
            for entry in ADD_IMPORTS:
                if entry['condition'] in content and entry['insert'] not in content:
                    match1 = re.match(r'^\s*package .+?;\s*', content)
                    if match1:
                        # package exists, add import after package
                        insert_pos = match1.end()
                        content = content[:insert_pos] + entry['insert'] + '\n' + content[insert_pos:]
                    else:
                        # package does not exist, add import after other imports
                        if 'import ' in content:
                            content = re.sub(r'(import .+;)', r'\1\n' + entry['insert'], content, count=1)
                        else:
                            # no import, add import after package
                            content = entry['insert'] + '\n\n' + content

            # write if content is changed
            if content != content2:
                with open(filepath, 'w', encoding='utf-8') as file:
                    file.write(content)

                print(f'Replaced content and added import (if needed) in {filepath}')

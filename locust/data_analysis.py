import os
import argparse
import csv

import seaborn as sns
import pandas as pd
import matplotlib.pyplot as plt

parser = argparse.ArgumentParser()
# path of the csv folder with data points
parser.add_argument("--experimentsFolder", "-e", default=os.path.join(".", "experiments"))

if __name__ == "__main__":
    # path of the csv
    args = parser.parse_args()
    data_csv_folder_path = args.experimentsFolder

    data = []
    experiment_output_files = os.listdir(data_csv_folder_path)
    for number_of_users in experiment_output_files:
        experiment_path = os.path.join(data_csv_folder_path, number_of_users)
        experiment_output_files = os.listdir(experiment_path)
        for experiment_output_file in experiment_output_files:
            if experiment_output_file.startswith('requests'):
                experiment_output_path = os.path.join(experiment_path, experiment_output_file)
                with open(experiment_output_path, 'r') as f:
                    reader = csv.DictReader(f)
                    for r in reader:
                        data.append({
                            'Total_users': int(number_of_users),
                            'Method': r['Method'],
                            'Name': r['Name'],
                            '# requests': int(r['# requests']),
                            '# failures': int(r['# failures']),
                            'Median response time': int(r['Median response time']),
                            'Average response time': int(r['Average response time']),
                            'Min response time': int(r['Min response time']),
                            'Max response time': int(r['Max response time']),
                            'Average Content Size': int( r['Average Content Size']),
                            'Requests/s': float(r['Requests/s'])
                        })

    df = pd.DataFrame(data)
    x = 'Total_users'
    y = 'Average response time'

    sns.lineplot(x, y, data=df, hue='Name', style='Name', markers=True)

    plt.show()
